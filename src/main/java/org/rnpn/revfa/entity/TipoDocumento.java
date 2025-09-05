package org.rnpn.revfa.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.persistence.*;
import org.rnpn.revfa.dto.TipoDocumentoDTO;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tipos_documentos")
public class TipoDocumento extends PanacheEntityBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_tipo_documento")
  public Long id;

  @Column(name = "id_superior")
  public Long idSuperior;

  @Column(name = "nombre")
  public String nombre;

  @Column(name = "plazo_respuesta")
  public Integer plazoRespuesta;

  @Column(name = "secuencia")
  public Integer secuencia;

  @Column(name = "activo")
  public Character activo;

  @Column(name = "tipo_correlativo")
  public String tipoCorrelativo;

  @Column(name = "etiqueta")
  public String etiqueta;

  @OneToMany(mappedBy = "tipoDocumento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  public List<Seccion> secciones;

  @OneToMany(mappedBy = "tipoDocumento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  public List<Solicitud> solicitudes;

  public static Uni<List<TipoDocumentoDTO>> findByIdSuperior(String id) {
    return find("SELECT td FROM TipoDocumento td WHERE td.idSuperior = ?1", id)
        .project(TipoDocumentoDTO.class).list();
  }

  public static Uni<TipoDocumento> findByIdTipoDocumento(Long id) {
    return TipoDocumento.findById(id);
  }

  public static Uni<String> generarCorrelativo(Long id) {
    int anioActual = LocalDate.now().getYear();

    return encontrarRaizRecursivo(id).flatMap(tipoDocumento -> {
      return Solicitud
          .count("tipoDocumento.id = ?1 AND EXTRACT(YEAR FROM fechaSolicitud) = ?2", id, anioActual)
          .map(conteo -> {
            long secuencia = conteo + 1;
            String correlativo = String.format("%s-%s-%d-%d", "REF", tipoDocumento.tipoCorrelativo,
                secuencia, anioActual);
            JsonObject correlativoObj = new JsonObject().put("correlativo", correlativo);

            JsonArray jsonArray = new JsonArray().add(correlativoObj);

            return jsonArray.encode();
          });
    });
  }

  public static Uni<TipoDocumento> encontrarRaizRecursivo(Long id) {
    return TipoDocumento.findById(id).flatMap(entity -> {
      TipoDocumento actual = (TipoDocumento) entity;

      if (actual == null) {
        return Uni.createFrom().failure(new RuntimeException("TipoDocumento no encontrado"));
      }

      if (actual.idSuperior == null || actual.idSuperior == 0) {
        return Uni.createFrom().item(actual);
      }

      return encontrarRaizRecursivo(actual.idSuperior);
    });
  }

  public static Uni<TipoDocumento> encontrarRaizConSecciones(Long id) {
    return encontrarRaizRecursivo(id).flatMap(
        raiz -> find("SELECT t FROM TipoDocumento t LEFT JOIN FETCH t.secciones WHERE t.id = ?1",
            raiz.id).firstResult().onItem().transform(entity -> (TipoDocumento) entity));
  }


}
