package org.rnpn.revfa.service;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.rnpn.revfa.dto.*;
import org.rnpn.revfa.entity.Seccion;
import org.rnpn.revfa.entity.TipoDocumento;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
  public Uni<List<StepperDTO>> findSteper(Long id) {
    return Seccion.findSteper(id);
  }


  @WithTransaction
  public Uni<List<SeccionesDTO>> seccionesAll(String id, String rol) {
    return Seccion.findSeccionesAll(id, rol);
  }

  @WithTransaction
  public Uni<List<SeccionesDTO>> seccionesPrueba(String id, String rol) {
    return Seccion.findSeccionesPrueba(id, rol);
  }


  @WithTransaction
  public Uni<List<SeccionesCompDTO>> dataAll(String id, String rol, String solicitud) {
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

  public Uni<List<DocumentosDTO>> getArbolTiposDocumentos() {
    return TipoDocumento.<TipoDocumento>listAll().onItem().transform(documentos -> {
      return construirArbol(documentos); // <-- aquí retornas la lista de DTOs
    });
  }

  private List<DocumentosDTO> construirArbol(List<TipoDocumento> documentos) {
    Map<Long, DocumentosDTO> mapa =
        documentos.stream().collect(Collectors.toMap(d -> d.id, DocumentosDTO::new));
    List<DocumentosDTO> raiz = new ArrayList<>();

    for (DocumentosDTO dto : mapa.values()) {
      if (dto.idSuperior != 0) {
        DocumentosDTO padre = mapa.get(dto.idSuperior);
        if (padre != null) {
          if (padre.subDocumentos == null) {
            padre.subDocumentos = new ArrayList<>();
          }
          padre.subDocumentos.add(dto);
        }
      } else {
        raiz.add(dto);
      }
    }
    return raiz;
  }
}
