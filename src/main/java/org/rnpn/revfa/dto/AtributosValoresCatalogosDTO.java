package org.rnpn.revfa.dto;

import org.rnpn.revfa.entity.AtributosValoresCatalogos;

public class AtributosValoresCatalogosDTO {
  public Long id;
  public String nombre;
  public String valor;
  public Character activo;

  public AtributosValoresCatalogosDTO(AtributosValoresCatalogos atributosValoresCatalogos) {
    this.id = atributosValoresCatalogos.id;
    this.nombre = atributosValoresCatalogos.nombre;
    this.valor = atributosValoresCatalogos.valor;
    this.activo = atributosValoresCatalogos.activo;
  }

  public AtributosValoresCatalogosDTO() {}
}
