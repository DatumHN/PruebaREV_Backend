package org.rnpn.revfa.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.rnpn.revfa.entity.Solicitud;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@RegisterForReflection
public class SolicitudCompDTO {
  public Long id;
  public String correlativo;
  public Date fechaSolicitud;

  public SolicitudCompDTO() {
    // constructor vacío requerido por Jackson
  }

  public SolicitudCompDTO(Solicitud solicitudes) {
    this.id = solicitudes.id;
    this.correlativo = solicitudes.correlativo;
    this.fechaSolicitud = solicitudes.fechaSolicitud;
  }
}
