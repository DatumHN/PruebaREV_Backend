package org.rnpn.revfa.optimized.dto;

import java.util.List;

public class CatalogoDTOOptimized {
  public Long id;
  public Long idSuperior;
  public String nombre;
  public Character activo;
  public Boolean subCatalogo;
  public List<ValorCatalogoDTOOptimized> valoresCatalogos;

  public CatalogoDTOOptimized() {}
}
