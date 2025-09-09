package org.rnpn.revfa.resource;

import java.util.List;

import jakarta.ws.rs.*;
import org.rnpn.revfa.dto.SolicitudDTO;
import org.rnpn.revfa.dto.SolicitudSimpleDTO;
import org.rnpn.revfa.entity.Solicitud;
import org.rnpn.revfa.service.SolicitudesService;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/solicitudes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolicitudesResource {

  private final SolicitudesService solicitudesService;

  public SolicitudesResource(SolicitudesService solicitudesService) {
    this.solicitudesService = solicitudesService;
  }

  @POST
  @Path("/crear")
  public Uni<Response> crearSolicitud(final Solicitud solicitudes) {
    return solicitudesService.crearSolicitud(solicitudes);
  }

  @PUT
  @Path("/actualizar/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> actualizarSolicitud(@PathParam("id") Long id, final Solicitud solicitud) {
    return solicitudesService.editarSolicitud(id, solicitud).onItem()
        .transform(s -> Response.ok(s).build()).onFailure().recoverWithItem(err -> {
          if (err instanceof WebApplicationException wae) {
            return Response.status(wae.getResponse().getStatus()).entity(wae.getMessage()).build();
          }
          return Response.serverError().entity(err.getMessage()).build();
        });
  }

  @PUT
  @Path("/estado/{estado}/solicitud/{idSolcitud}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> actualizarEstado(@PathParam("estado") String estado,
      @PathParam("idSolcitud") Long idSolcitud) {
    return solicitudesService.editarEstado(estado, idSolcitud)
        .replaceWith(Response.ok("{\"message\":\"Estado actualizado con éxito\"}").build())
        .onFailure().recoverWithItem(err -> {
          if (err instanceof WebApplicationException wae) {
            return Response.status(wae.getResponse().getStatus())
                .entity("{\"error\":\"" + wae.getMessage() + "\"}").build();
          }
          return Response.serverError().entity("{\"error\":\"" + err.getMessage() + "\"}").build();
        });
  }


  @GET
  public Uni<List<SolicitudDTO>> getSolicitudes() {
    return solicitudesService.getSolicitudes();
  }
}
