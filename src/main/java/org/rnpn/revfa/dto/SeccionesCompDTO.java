package org.rnpn.revfa.dto;

import org.rnpn.revfa.entity.Seccion;

import java.util.List;
import java.util.stream.Collectors;

public class SeccionesCompDTO {
  public Long id;
  public String nombre;
  public Integer secuencia;
  public Character activo;
  public Long idSuperior;
  public List<CampoSeccionCompDTO> camposSecciones;
  public TipoDocumentoDTO tipoDocumento;
  public List<SeccionesCompDTO> subSecciones;
  public Character ventanaEmergente;
  public EtapaDTO etapa;

  public SeccionesCompDTO(Seccion seccion) {
    if (seccion == null) {
      return;
    }
    this.id = seccion.id;
    this.idSuperior = seccion.idSuperior;
    this.nombre = seccion.nombre;
    this.secuencia = seccion.secuencia;
    this.activo = seccion.activo;
    this.ventanaEmergente = seccion.ventanaEmergente;
    if (seccion.etapa != null) {
      this.etapa = new EtapaDTO(seccion.etapa.id, seccion.etapa.nombre, seccion.etapa.activo);
    }
    if (seccion.camposSecciones != null) {
      this.camposSecciones = seccion.camposSecciones.stream().map(CampoSeccionCompDTO::new)
          .collect(Collectors.toList());
    }

  }

}
