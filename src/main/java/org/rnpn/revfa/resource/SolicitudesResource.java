package org.rnpn.revfa.resource;

import java.util.List;

import org.rnpn.revfa.dto.SolicitudDTO;
import org.rnpn.revfa.entity.Solicitud;
import org.rnpn.revfa.service.SolicitudesService;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
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

  @GET
  public Uni<List<SolicitudDTO>> getSolicitudes(@QueryParam("id") String id) {
    return solicitudesService.getSolicitudes(id);
  }
}
