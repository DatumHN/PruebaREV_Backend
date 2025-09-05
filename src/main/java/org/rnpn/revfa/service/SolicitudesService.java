package org.rnpn.revfa.service;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.rnpn.revfa.dto.RespuestaCreacionDTO;
import org.rnpn.revfa.dto.SolicitudDTO;
import org.rnpn.revfa.entity.Solicitud;
import org.rnpn.revfa.mapper.SolicitudMapper;

import java.util.List;

@ApplicationScoped
public class SolicitudesService {
  @Inject
  SolicitudMapper solicitudMapper;

  @WithTransaction
  public Uni<List<SolicitudDTO>> getSolicitudes(String id) {
    return Solicitud.findSolicitudes(id);
  }

  @WithTransaction
  public Uni<Response> crearSolicitud(final Solicitud solicitudes) {
    SolicitudDTO soli = solicitudMapper.toDto(solicitudes);
    return solicitudMapper.toEntity(soli).persist().map(created -> {
      RespuestaCreacionDTO respuesta =
          new RespuestaCreacionDTO("creacion de solicitud exitosa", soli);

      return Response.status(Response.Status.CREATED).entity(respuesta).build();
    }).onFailure().recoverWithItem(ex -> {
      ex.printStackTrace();
      return Response.serverError()
          .entity(new RespuestaCreacionDTO(
              "Ocurrió un error al crear la solicitud, contáctese con el administrador", null))
          .build();
    });

  }

}
