package org.rnpn.revfa.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class EtapaDTO {
  public Long id;
  public String nombre;
  public Character activo;

  public EtapaDTO(Long id, String nombre, Character activo) {
    this.id = id;
    this.nombre = nombre;
    this.activo = activo;
  }
}
