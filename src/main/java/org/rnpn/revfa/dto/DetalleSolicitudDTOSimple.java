package org.rnpn.revfa.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.rnpn.revfa.entity.DetalleSolicitud;

@RegisterForReflection
public class DetalleSolicitudDTOSimple {
  public Long id;
  public String valor;
  public Long campoSeccion;
  public SolicitudCompDTO solicitud;


  public DetalleSolicitudDTOSimple(DetalleSolicitud detallesSolicitudes) {
    this.id = detallesSolicitudes.id;
    this.valor = detallesSolicitudes.valor;
    this.campoSeccion = detallesSolicitudes.campoSeccion;
    this.solicitud =
        detallesSolicitudes.solicitud != null ? new SolicitudCompDTO(detallesSolicitudes.solicitud)
            : null;
  }

  public DetalleSolicitudDTOSimple() {}


}
