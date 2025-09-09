package org.rnpn.revfa.optimized.dto;

import java.util.List;

public class SeccionesDTOOptimized {
  public Long id;
  public String nombre;
  public Integer secuencia;
  public Character activo;
  public Long idSuperior;
  public List<CampoSeccionDTOOptimized> camposSecciones;
  public TipoDocumentoDTOOptimized tipoDocumento;
  public List<SeccionesDTOOptimized> subSecciones;
  public Character ventanaEmergente;
  public EtapaDTOOptimized etapa;

  public SeccionesDTOOptimized() {}
}
