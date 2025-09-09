package org.rnpn.revfa.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.rnpn.revfa.entity.CampoSeccion;

import java.util.List;
import java.util.stream.Collectors;

@RegisterForReflection
public class CampoSeccionCompDTO {
  public Long id;
  public Integer secuencia;
  public Character activo;
  public CampoCompDTO campo;
  public List<RolPermisoCampoSeccionDTO> rolesPermisos;

  public CampoSeccionCompDTO(CampoSeccion campoSeccion) {
    this.id = campoSeccion.id;
    this.secuencia = campoSeccion.secuencia;
    this.activo = campoSeccion.activo;
    this.campo = campoSeccion.campos != null ? new CampoCompDTO(campoSeccion.campos) : null;

    if (campoSeccion.rolesPermisosCamposSecciones != null) {
      this.rolesPermisos = campoSeccion.rolesPermisosCamposSecciones.stream()
          .map(RolPermisoCampoSeccionDTO::new).collect(Collectors.toList());
    }
  }

}
