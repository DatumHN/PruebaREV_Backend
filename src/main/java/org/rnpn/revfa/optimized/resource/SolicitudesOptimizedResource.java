package org.rnpn.revfa.optimized.resource;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.rnpn.revfa.optimized.dto.SolicitudDTOOptimized;
import org.rnpn.revfa.optimized.service.SolicitudesOptimizedService;

import java.util.List;

@Path("/solicitudes-optimized")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolicitudesOptimizedResource {

  @Inject
  SolicitudesOptimizedService solicitudesOptimizedService;

  @GET
  public Uni<List<SolicitudDTOOptimized>> getSolicitudes() {
    return solicitudesOptimizedService.getSolicitudes();
  }

  @PUT
  @Path("/estado/{estado}/solicitud/{idSolicitud}")
  public Uni<Response> actualizarEstado(@PathParam("estado") String estado,
      @PathParam("idSolicitud") Long idSolicitud) {
    return solicitudesOptimizedService.actualizarEstado(estado, idSolicitud)
        .replaceWith(Response.ok("{\"message\":\"Estado actualizado con éxito\"}").build())
        .onFailure().recoverWithItem(err -> {
          if (err instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"Solicitud no encontrada\"}").build();
          }
          return Response.serverError().entity("{\"error\":\"" + err.getMessage() + "\"}").build();
        });
  }

  @PUT
  @Path("/actualizar/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> actualizarSolicitud(@PathParam("id") Long id,
      SolicitudDTOOptimized solicitud) {
    return solicitudesOptimizedService.actualizarSolicitud(id, solicitud)
        .map(message -> Response.ok("{\"message\":\"" + message + "\"}").build()).onFailure()
        .recoverWithItem(err -> {
          if (err instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"Solicitud no encontrada\"}").build();
          }
          err.printStackTrace();
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
              "{\"error\":\"Ocurrió un error al actualizar la solicitud, contáctese con el administrador\"}")
              .build();
        });
  }
}
