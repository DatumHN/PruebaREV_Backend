package org.rnpn.revfa.service;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.rnpn.revfa.dto.RespuestaCreacionDTO;
import org.rnpn.revfa.dto.SolicitudDTO;
import org.rnpn.revfa.dto.SolicitudSimpleDTO;
import org.rnpn.revfa.entity.DetalleSolicitud;
import org.rnpn.revfa.entity.Solicitud;
import org.rnpn.revfa.mapper.SolicitudMapper;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class SolicitudesService {
  @Inject
  SolicitudMapper solicitudMapper;

  @WithTransaction
  public Uni<List<SolicitudDTO>> getSolicitudes() {
    return Solicitud.count().onItem().transformToUni(count -> {
      // If count is 0, return an empty list Uni.
      if (count == 0) {
        return Uni.createFrom().item(Collections.emptyList());
      }
      // Otherwise, proceed with the original logic.
      return Solicitud.findSolicitudes();
    });
  }

  @WithTransaction
  public Uni<Response> crearSolicitud(final Solicitud solicitudes) {
    SolicitudDTO soli = solicitudMapper.toDto(solicitudes);
    solicitudes.estado = "Pendiente";
    return Solicitud.persist(solicitudes).map(created -> {
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

  @WithTransaction
  public Uni<Response> editarSolicitud(Long id, Solicitud solicitudNueva) {
    return Solicitud.<Solicitud>findById(id).onItem().ifNull()
        .failWith(
            () -> new WebApplicationException("Solicitud no encontrada", Response.Status.NOT_FOUND))
        .flatMap(solicitudExistente -> {
          solicitudExistente.estado = solicitudNueva.estado;
          // Manejo de detalles
          if (solicitudNueva.detallesSolicitudes != null) {
            for (DetalleSolicitud detalleNuevo : solicitudNueva.detallesSolicitudes) {
              if (detalleNuevo.id == null) {
                detalleNuevo.solicitud = solicitudExistente;
                solicitudExistente.detallesSolicitudes.add(detalleNuevo);
              } else {
                solicitudExistente.detallesSolicitudes.stream()
                    .filter(d -> d.id.equals(detalleNuevo.id)).findFirst()
                    .ifPresent(detalleExistente -> {
                      detalleExistente.valor = detalleNuevo.valor;
                      detalleExistente.valoresCatalogos = detalleNuevo.valoresCatalogos;
                    });
              }
            }
          }

          // Solo mensaje de éxito
          return Uni.createFrom().item(Response.ok("Actualización exitosa").build());
        }).onFailure().recoverWithItem(ex -> {
          ex.printStackTrace(); // aquí ves la causa real en consola
          return Response.serverError()
              .entity(
                  "Ocurrió un error al actualizar la solicitud, contáctese con el administrador")
              .build();
        });
  }

  @WithTransaction
  public Uni<Void> editarEstado(String estado, Long id) {
    return Solicitud.<Solicitud>findById(id).onItem().ifNull()
        .failWith(
            () -> new WebApplicationException("Solicitud no encontrada", Response.Status.NOT_FOUND))
        .invoke(solicitud -> solicitud.estado = estado) // actualizar campo
        .replaceWithVoid(); // devolvemos Uni<Void>
  }

}
