package org.rnpn.revfa.dto;

import io.smallrye.mutiny.Uni;
import org.rnpn.revfa.entity.Catalogos;

import java.util.List;
import java.util.stream.Collectors;

public class CatalogosDTO {
  public Long id;
  public Long idSuperior;
  public String nombre;
  public Character activo;
  public List<ValoresCatalogosDTO> valoresCatalogos;
  public Boolean subCatalogo;

  public CatalogosDTO(Catalogos catalogos) {
    this.id = catalogos.id;
    this.idSuperior = catalogos.idSuperior;
    this.nombre = catalogos.nombre;
    this.activo = catalogos.activo;
    this.valoresCatalogos = catalogos.valoresCatalogos.stream().map(ValoresCatalogosDTO::new)
        .collect(Collectors.toList());
  }

  public CatalogosDTO() {}

  public Uni<Boolean> recursivo(Long id) {
    return Catalogos.count("idSuperior = ?1", id).map(count -> count > 0);
  }

  @Override
  public String toString() {
    return "CatalogosDTO{" + "id=" + id + ", idSuperior=" + idSuperior + ", nombre='" + nombre
        + '\'' + ", activo=" + activo + ", valoresCatalogos=" + valoresCatalogos + ", subCatalogo="
        + subCatalogo + '}';
  }
}
