package org.rnpn.revfa.optimized.dto;

import java.util.List;

public class CampoDTOOptimized {
  public Long id;
  public String nombre;
  public String tipo;
  public String valoresPosibles;
  public String valorPredeterminado;
  public Character obligatorio;
  public String ancho;
  public String ejemplo;
  public String ayuda;
  public Character activo;
  public String mascara;
  public String validCompleja;
  public Character busqueda;
  public Integer lonMinima;
  public Integer lonMaxima;
  public CatalogoDTOOptimized catalogos;
  public List<DetalleSolicitudDTOOptimized> detallesSolicitudes;

  public CampoDTOOptimized() {}
}
