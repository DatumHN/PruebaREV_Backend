package org.rnpn.revfa.optimized.service;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;
import org.rnpn.revfa.entity.TipoDocumento;
import org.rnpn.revfa.optimized.dto.DetalleSolicitudDTOOptimized;
import org.rnpn.revfa.optimized.dto.SolicitudDTOOptimized;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SolicitudesOptimizedService {

  @Inject
  Mutiny.SessionFactory sessionFactory;

  @WithTransaction
  public Uni<List<SolicitudDTOOptimized>> getSolicitudes() {
    String query = """
        SELECT
            s.ID_SOLICITUD,
            s.CORRELATIVO,
            s.ESTADO_SOLICITUD,
            s.UUID,
            s.FECHA_SOLICITUD,
            s.ID_TIPO_DOCUMENTO,
            td.NOMBRE as TIPO_DOC_NOMBRE,
            td.ACTIVO as TIPO_DOC_ACTIVO,
            td.ID_SUPERIOR as TIPO_DOC_SUPERIOR
        FROM SOLICITUDES s
        LEFT JOIN TIPOS_DOCUMENTOS td ON s.ID_TIPO_DOCUMENTO = td.ID_TIPO_DOCUMENTO
        ORDER BY s.FECHA_SOLICITUD DESC
        """;

    return sessionFactory.withSession(session -> session.createNativeQuery(query).getResultList()
        .map(results -> this.buildSolicitudesList(results)));
  }

  private List<SolicitudDTOOptimized> buildSolicitudesList(List<?> results) {
    List<SolicitudDTOOptimized> solicitudes = new ArrayList<>();

    for (Object result : results) {
      Object[] row = (Object[]) result;

      SolicitudDTOOptimized solicitud = new SolicitudDTOOptimized();
      solicitud.id = row[0] != null ? ((Number) row[0]).longValue() : null;
      solicitud.correlativo = (String) row[1];
      solicitud.estado = (String) row[2];
      solicitud.uuid = (String) row[3];
      solicitud.fechaSolicitud =
          row[4] != null ? new java.sql.Date(((java.sql.Timestamp) row[4]).getTime()) : null;

      // Build TipoDocumento entity
      if (row[5] != null) {
        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.id = ((Number) row[5]).longValue();
        tipoDocumento.nombre = (String) row[6];
        tipoDocumento.activo = row[7] != null ? ((String) row[7]).charAt(0) : null;
        tipoDocumento.idSuperior = row[8] != null ? ((Number) row[8]).longValue() : null;

        solicitud.tipoDocumento = tipoDocumento;
      }

      solicitudes.add(solicitud);
    }

    return solicitudes;
  }

  @WithTransaction
  public Uni<Void> actualizarEstado(String estado, Long idSolicitud) {
    String updateQuery = """
        UPDATE SOLICITUDES
        SET ESTADO_SOLICITUD = :estado
        WHERE ID_SOLICITUD = :idSolicitud
        """;

    return sessionFactory.withTransaction(
        (session, tx) -> session.createNativeQuery(updateQuery).setParameter("estado", estado)
            .setParameter("idSolicitud", idSolicitud).executeUpdate().map(rowsAffected -> {
              if (rowsAffected == 0) {
                throw new jakarta.ws.rs.NotFoundException("Solicitud no encontrada");
              }
              return null;
            }));
  }

  public Uni<String> actualizarSolicitud(Long id, SolicitudDTOOptimized solicitudNueva) {
    return sessionFactory.withTransaction((session, tx) -> {
      // 1. Verificar que la solicitud existe y actualizar datos básicos
      String updateSolicitudQuery = """
          UPDATE SOLICITUDES
          SET ESTADO_SOLICITUD = :estado
          WHERE ID_SOLICITUD = :id
          """;

      return session.createNativeQuery(updateSolicitudQuery)
          .setParameter("estado", solicitudNueva.estado).setParameter("id", id).executeUpdate()
          .flatMap(rowsAffected -> {
            if (rowsAffected == 0) {
              throw new jakarta.ws.rs.NotFoundException("Solicitud no encontrada");
            }

            // 2. Manejar detalles de solicitud si existen
            if (solicitudNueva.detallesSolicitudes != null
                && !solicitudNueva.detallesSolicitudes.isEmpty()) {
              return actualizarDetallesSolicitud(session, id, solicitudNueva.detallesSolicitudes);
            }

            return Uni.createFrom().item("Actualización exitosa");
          });
    });
  }

  private Uni<String> actualizarDetallesSolicitud(Mutiny.Session session, Long solicitudId,
      List<DetalleSolicitudDTOOptimized> detalles) {

    List<Uni<Integer>> operations = new ArrayList<>();

    for (DetalleSolicitudDTOOptimized detalle : detalles) {
      if (detalle.id == null) {
        // Crear nuevo detalle
        String insertQuery =
            """
                INSERT INTO DETALLES_SOLICITUDES (ID_CAMPO, ID_SOLICITUD, VALOR, CAMPO_SECCION, ID_VALOR_CATALOGO)
                VALUES (:idCampo, :idSolicitud, :valor, :campoSeccion, :idValorCatalogo)
                """;

        var insertOp = session.createNativeQuery(insertQuery)
            // .setParameter("idCampo", detalle.campos != null ? detalle.campos.id : null)
            .setParameter("idSolicitud", solicitudId).setParameter("valor", detalle.valor)
            .setParameter("campoSeccion", detalle.campoSeccion)
            .setParameter("idCampo", detalle.campos.id)
            .setParameter("idValorCatalogo",
                detalle.valoresCatalogos != null ? detalle.valoresCatalogos.id : null)
            .executeUpdate();

        operations.add(insertOp);
      } else {
        // Actualizar detalle existente
        String updateQuery = """
            UPDATE DETALLES_SOLICITUDES
            SET VALOR = :valor,
                ID_VALOR_CATALOGO = :idValorCatalogo
            WHERE ID_DETALLE_SOLICITUD = :id
            AND ID_SOLICITUD = :idSolicitud
            """;

        var updateOp = session.createNativeQuery(updateQuery).setParameter("valor", detalle.valor)
            .setParameter("idValorCatalogo",
                detalle.valoresCatalogos != null ? detalle.valoresCatalogos.id : null)
            .setParameter("id", detalle.id).setParameter("idSolicitud", solicitudId)
            .executeUpdate();

        operations.add(updateOp);
      }
    }

    // Ejecutar todas las operaciones
    return Uni.combine().all().unis(operations)
        .combinedWith(results -> "Detalles actualizados correctamente");
  }
}
