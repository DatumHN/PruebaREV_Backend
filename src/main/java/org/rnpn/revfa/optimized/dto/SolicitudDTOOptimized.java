package org.rnpn.revfa.optimized.dto;

import org.rnpn.revfa.entity.TipoDocumento;

import java.sql.Date;
import java.util.List;

public class SolicitudDTOOptimized {
  public Long id;
  public String correlativo;
  public String uuid;
  public String estado;
  public Date fechaSolicitud;
  public TipoDocumento tipoDocumento;
  public List<DetalleSolicitudDTOOptimized> detallesSolicitudes;

  public SolicitudDTOOptimized() {}
}
