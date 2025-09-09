package org.rnpn.revfa.dto;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.rnpn.revfa.entity.Solicitud;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class SolicitudDTO {
  public Long id;
  public String correlativo;
  public Date fechaSolicitud;
  public TipoDocumentoDTO tipoDocumento;
  public List<DetalleSolicitudDTO> detallesSolicitudes;
  public String uuid;
  public String estado;

  public SolicitudDTO() {
    // constructor vacío requerido por Jackson
  }

  public SolicitudDTO(Solicitud solicitudes) {
    this.id = solicitudes.id;
    this.correlativo = solicitudes.correlativo;
    this.fechaSolicitud = solicitudes.fechaSolicitud;
    this.uuid = solicitudes.uuid;
    this.estado = solicitudes.estado;
    this.tipoDocumento =
        solicitudes.tipoDocumento != null ? new TipoDocumentoDTO(solicitudes.tipoDocumento) : null;
    this.detallesSolicitudes = solicitudes.detallesSolicitudes.stream()
        .map(DetalleSolicitudDTO::new).collect(Collectors.toList());

  }
}
