package org.rnpn.revfa.optimized.dto;

import org.rnpn.revfa.entity.Campo;
import org.rnpn.revfa.entity.Solicitud;
import org.rnpn.revfa.entity.ValoresCatalogos;

public class DetalleSolicitudDTOOptimized {
  public Long id;
  public String valor;
  public Campo campos;
  public Long campoSeccion;
  public Solicitud solicitud;
  public ValoresCatalogos valoresCatalogos;

  public DetalleSolicitudDTOOptimized() {}
}
