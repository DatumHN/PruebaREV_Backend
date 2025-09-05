package org.rnpn.revfa.dto;

import org.rnpn.revfa.entity.DetalleSolicitud;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class DetalleSolicitudDTO {
  public Long id;
  public String valor;
  public CampoDTO campos;
  public SolicitudDTO solicitud;

  public DetalleSolicitudDTO(DetalleSolicitud detallesSolicitudes) {
    this.id = detallesSolicitudes.id;
    this.valor = detallesSolicitudes.valor;
    this.campos =
        detallesSolicitudes.campos != null ? new CampoDTO(detallesSolicitudes.campos) : null;
  }

  public DetalleSolicitudDTO() {}
}
