package org.rnpn.revfa.dto.solicitudes;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.rnpn.revfa.dto.SeccionesDTO;
import org.rnpn.revfa.entity.Seccion;
import org.rnpn.revfa.entity.TipoDocumento;

import java.util.List;

@RegisterForReflection
public class TipoDocumentoSolicitudDTO {
  public Long id;
  public Long idSuperior;
  public String nombre;
  public Integer plazoRespuesta;
  public Integer secuencia;
  public Character activo;
  public String tipoCorrelativo;
  public String etiqueta;
  public List<SeccionesDTO> secciones;

  public TipoDocumentoSolicitudDTO() {}

  public TipoDocumentoSolicitudDTO(TipoDocumento tipoDocumento) {
    this.id = tipoDocumento.id;
    this.idSuperior = tipoDocumento.idSuperior;
    this.nombre = tipoDocumento.nombre;
    this.plazoRespuesta = tipoDocumento.plazoRespuesta;
    this.secuencia = tipoDocumento.secuencia;
    this.activo = tipoDocumento.activo;
    this.tipoCorrelativo = tipoDocumento.tipoCorrelativo;
    this.etiqueta = tipoDocumento.etiqueta;
    this.secciones = tipoDocumento.secciones.stream().map(SeccionesDTO::new).toList();
  }



}
