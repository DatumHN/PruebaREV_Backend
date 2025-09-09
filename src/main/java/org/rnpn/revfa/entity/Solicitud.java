package org.rnpn.revfa.entity;

import java.sql.Date;
import java.util.List;

import org.rnpn.revfa.dto.SolicitudDTO;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.rnpn.revfa.dto.SolicitudSimpleDTO;

@Entity
@Table(name = "solicitudes")
public class Solicitud extends PanacheEntityBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_solicitud")
  public Long id;

  @Column(name = "correlativo")
  public String correlativo;

  @Column(name = "uuid")
  public String uuid;

  @Column(name = "estado_solicitud")
  public String estado;

  @Column(name = "fecha_solicitud")
  public Date fechaSolicitud;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_tipo_documento")
  public TipoDocumento tipoDocumento;

  @OneToMany(mappedBy = "solicitud", fetch = FetchType.EAGER,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  public List<DetalleSolicitud> detallesSolicitudes;

  @PrePersist
  public void asignarReferencias() {
    if (detallesSolicitudes != null) {
      for (DetalleSolicitud detalle : detallesSolicitudes) {
        detalle.solicitud = this;
      }
    }
  }

  public static Uni<List<SolicitudDTO>> findSolicitudes() {
    Uni<List<Solicitud>> solicitudes = find("SELECT DISTINCT s FROM Solicitud s "
        + "LEFT JOIN FETCH s.detallesSolicitudes ds " + "LEFT JOIN FETCH ds.campos c "
        + "WHERE c.id in (3,4,5,6,7,8,9) " + "ORDER BY s.id ASC").list();

    return solicitudes.onItem().transformToUni(list -> {
      // Use Uni.combine().all() to process each Solicitud item asynchronously
      List<Uni<Solicitud>> processedUnis = list.stream().map(Solicitud::filterAndProcessDetails) // Use
                                                                                                 // a
                                                                                                 // separate
                                                                                                 // method
          .toList();

      // Combine all processed Unis and return the list
      return Uni.combine().all().unis(processedUnis)
          .with(items -> items.stream().map(item -> (Solicitud) item).toList());
    }).onItem().transform(list -> list.stream().map(SolicitudDTO::new).toList());
  }

  // Separate method to process a single Solicitud reactively
  private static Uni<Solicitud> filterAndProcessDetails(Solicitud s) {
    // Process each detalleSolicitud asynchronously
    List<Uni<DetalleSolicitud>> unis =
        s.detallesSolicitudes.stream().filter(ds -> ds.campoSeccion != null) // Pre-filter based on
                                                                             // the existence of
                                                                             // campoSeccion
            .map(ds -> CampoSeccion.findById(ds.campoSeccion).onItem().transform(cs -> {
              // Apply filter logic after fetching
              if (cs != null && ((CampoSeccion) cs).seccion.id == 1) {
                ds.campos.detallesSolicitudes = null;
                System.out.println(cs);
                return ds;
              }
              return null; // Return null to be filtered out
            })).toList();

    // Use a Uni for the case where there are no campoSeccion to filter
    if (unis.isEmpty()) {
      return Uni.createFrom().item(s);
    }

    // Combine the Unis and then update the solicitud
    return Uni.combine().all().unis(unis).with(results -> results.stream()
        .filter(item -> item != null).map(item -> (DetalleSolicitud) item).toList()).onItem()
        .transform(filteredList -> {
          s.detallesSolicitudes = filteredList;
          return s;
        });
  }
}
