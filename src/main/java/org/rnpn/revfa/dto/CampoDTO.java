package org.rnpn.revfa.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.rnpn.revfa.entity.Campo;

import java.sql.Clob;
import java.util.List;
import java.util.stream.Collectors;

@RegisterForReflection
public class CampoDTO {
  public Long id;
  public String nombre;
  public String tipo;
  public Clob valoresPosibles;
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
  public CatalogosDTO catalogos;
  // public List<DetalleSolicitudDTOSimple> detalleSolicitudDTOSimple;

  public CampoDTO() {}

  public CampoDTO(Campo campo) {
    this.id = campo.id;
    this.nombre = campo.nombre;
    this.tipo = campo.tipo;
    this.valoresPosibles = campo.valoresPosibles;
    this.valorPredeterminado = campo.valorPredeterminado;
    this.obligatorio = campo.obligatorio;
    this.ancho = campo.ancho;
    this.ejemplo = campo.ejemplo;
    this.ayuda = campo.ayuda;
    this.activo = campo.activo;
    this.mascara = campo.mascara;
    this.validCompleja = campo.validCompleja;
    this.busqueda = campo.busqueda;
    this.lonMinima = campo.lonMinima;
    this.lonMaxima = campo.lonMaxima;
    if (campo.catalogos != null) {
      this.catalogos = campo.catalogos != null ? new CatalogosDTO(campo.catalogos) : null;
    }
    /*
     * if (campo.detallesSolicitudes != null) { this.detalleSolicitudDTOSimple =
     * campo.detallesSolicitudes.stream()
     * .map(DetalleSolicitudDTOSimple::new).collect(Collectors.toList()); }
     */

  }

}
