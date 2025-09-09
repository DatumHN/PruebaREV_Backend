package org.rnpn.revfa.optimized.dto;

import java.util.List;

public class CampoSeccionDTOOptimized {
  public Long id;
  public Integer secuencia;
  public Character activo;
  public CampoDTOOptimized campo;
  public List<RolPermisoDTOOptimized> rolesPermisos;

  public CampoSeccionDTOOptimized() {}
}
