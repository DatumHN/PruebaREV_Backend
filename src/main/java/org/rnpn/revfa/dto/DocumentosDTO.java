package org.rnpn.revfa.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.rnpn.revfa.entity.TipoDocumento;

import java.util.List;

@RegisterForReflection
public class DocumentosDTO {
  public Long id;
  public Long idSuperior;
  public String nombre;
  public Integer plazoRespuesta;
  public Integer secuencia;
  public Character activo;
  public String tipoCorrelativo;
  public String etiqueta;
  public List<DocumentosDTO> subDocumentos;

  public DocumentosDTO() {}

  public DocumentosDTO(TipoDocumento tipoDocumento) {
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
