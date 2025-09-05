package org.rnpn.revfa.service;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.rnpn.revfa.dto.SeccionesDTO;
import org.rnpn.revfa.dto.TipoDocumentoDTO;
import org.rnpn.revfa.entity.Seccion;
import org.rnpn.revfa.entity.TipoDocumento;

import java.util.List;

@ApplicationScoped
public class TipoDocumentoService {
  @WithTransaction
  public Uni<List<TipoDocumentoDTO>> getTiposDocumentosByIdSuperior(String id) {
    return TipoDocumento.findByIdSuperior(id);
  }

  @WithTransaction
  public Uni<List<SeccionesDTO>> findByDocSecciones(String id) {
    return Seccion.findByDocSecciones(id);
  }


  @WithTransaction
  public Uni<List<SeccionesDTO>> seccionesAll(String id, String rol) {
    return Seccion.findSeccionesAll(id, rol);
  }

  @WithTransaction
  public Uni<List<SeccionesDTO>> dataAll(String id, String rol, String solicitud) {
    return Seccion.findDataAll(id, rol, solicitud);
  }

  @WithTransaction
  public Uni<String> Correlativo(Long id) {
    return TipoDocumento.generarCorrelativo(id);
  }

  @WithTransaction
  public Uni<TipoDocumento> encontrarRaizRecursivo(Long id) {
    return TipoDocumento.encontrarRaizRecursivo(id);
  }

}
