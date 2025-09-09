package org.rnpn.revfa.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.rnpn.revfa.dto.solicitudes.TipoDocumentoSolicitudDTO;
import org.rnpn.revfa.entity.Solicitud;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@RegisterForReflection
public class SolicitudSimpleDTO {
  public Long id;
  public String correlativo;
  public Date fechaSolicitud;
  public TipoDocumentoSolicitudDTO tipoDocumento;
  public String uuid;
  public String estado;

  public SolicitudSimpleDTO() {
    // constructor vacío requerido por Jackson
  }

  public SolicitudSimpleDTO(Solicitud solicitudes) {
    this.id = solicitudes.id;
    this.correlativo = solicitudes.correlativo;
    this.fechaSolicitud = solicitudes.fechaSolicitud;
    this.uuid = solicitudes.uuid;
    this.estado = solicitudes.estado;
    this.tipoDocumento =
        solicitudes.tipoDocumento != null ? new TipoDocumentoSolicitudDTO(solicitudes.tipoDocumento)
            : null;
  }
}
