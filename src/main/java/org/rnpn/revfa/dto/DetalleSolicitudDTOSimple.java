package org.rnpn.revfa.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.rnpn.revfa.entity.DetalleSolicitud;

@RegisterForReflection
public class DetalleSolicitudDTOSimple {
  public Long id;
  public String valor;

  public DetalleSolicitudDTOSimple(DetalleSolicitud detallesSolicitudes) {
    this.id = detallesSolicitudes.id;
    this.valor = detallesSolicitudes.valor;
  }

  public DetalleSolicitudDTOSimple() {}


}
