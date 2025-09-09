package org.rnpn.revfa.dto;

import org.rnpn.revfa.entity.Seccion;

public class StepperDTO {
  public Long id;
  public String nombre;
  public Integer secuencia;
  public Character activo;
  public Long idSuperior;

  public StepperDTO(Seccion seccion) {
    if (seccion == null) {
      return;
    }
    this.id = seccion.id;
    this.idSuperior = seccion.idSuperior;
    this.nombre = seccion.nombre;
    this.secuencia = seccion.secuencia;
    this.activo = seccion.activo;


  }

}
