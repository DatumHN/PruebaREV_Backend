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

  @OneToMany(mappedBy = "solicitud", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  public List<DetalleSolicitud> detallesSolicitudes;

  @PrePersist
  public void asignarReferencias() {
    if (detallesSolicitudes != null) {
      for (DetalleSolicitud detalle : detallesSolicitudes) {
        detalle.solicitud = this;
      }
    }
  }

  public static Uni<List<SolicitudDTO>> findSolicitudes(String id) {
    Uni<List<Solicitud>> solicitudes = find(
        "SELECT s FROM Solicitud s LEFT JOIN FETCH s.detallesSolicitudes ds LEFT JOIN FETCH ds.campos WHERE s.tipoDocumento.id = ?1 ORDER BY s.id ASC",
        id).list();

    return solicitudes.map(list -> list.stream().map(SolicitudDTO::new).toList());
  }

}
