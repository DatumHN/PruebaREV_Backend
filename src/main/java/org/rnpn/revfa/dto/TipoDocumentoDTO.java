package org.rnpn.revfa.dto;

import org.rnpn.revfa.entity.TipoDocumento;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TipoDocumentoDTO {
  public Long id;
  public Long idSuperior;
  public String nombre;
  public Integer plazoRespuesta;
  public Integer secuencia;
  public Character activo;
  public String tipoCorrelativo;
  public String etiqueta;

  public TipoDocumentoDTO() {}

  public TipoDocumentoDTO(TipoDocumento tipoDocumento) {
    this.id = tipoDocumento.id;
    this.idSuperior = tipoDocumento.idSuperior;
    this.nombre = tipoDocumento.nombre;
    this.plazoRespuesta = tipoDocumento.plazoRespuesta;
    this.secuencia = tipoDocumento.secuencia;
    this.activo = tipoDocumento.activo;
    this.tipoCorrelativo = tipoDocumento.tipoCorrelativo;
    this.etiqueta = tipoDocumento.etiqueta;
  }



}
