package org.rnpn.revfa.dto;

import org.rnpn.revfa.entity.ValoresCatalogos;

import java.util.List;
import java.util.stream.Collectors;

public class ValoresCatalogosDTO {
  public Long id;
  public Long idSuperior;
  public String valor;
  public Character activo;
  public List<AtributosValoresCatalogosDTO> atributosValoresCatalogos;

  public ValoresCatalogosDTO(ValoresCatalogos valoresCatalogos) {
    if (valoresCatalogos != null) {
      this.id = valoresCatalogos.id;
      this.idSuperior = valoresCatalogos.idSuperior;
      this.valor = valoresCatalogos.valor;
      this.activo = valoresCatalogos.activo;
      this.atributosValoresCatalogos = valoresCatalogos.atributosValoresCatalogos.stream()
          .map(AtributosValoresCatalogosDTO::new).collect(Collectors.toList());
    }
  }
}
