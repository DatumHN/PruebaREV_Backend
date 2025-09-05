package org.rnpn.revfa.dto;

import org.rnpn.revfa.entity.RolPermisoCampoSeccion;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class RolPermisoCampoSeccionDTO {
  public Long id;
  public Long idRol;
  public String nombreRol;
  public Character permiso;
  public Character activo;

  public RolPermisoCampoSeccionDTO(RolPermisoCampoSeccion rolPermisoCampoSeccion) {
    if (rolPermisoCampoSeccion == null)
      return;

    this.id = rolPermisoCampoSeccion.id;
    this.activo = rolPermisoCampoSeccion.activo;
    this.permiso = rolPermisoCampoSeccion.permiso;

    if (rolPermisoCampoSeccion.rol != null) {
      this.idRol = rolPermisoCampoSeccion.rol.id;
      this.nombreRol = rolPermisoCampoSeccion.rol.nombre;
    }

  }
}
