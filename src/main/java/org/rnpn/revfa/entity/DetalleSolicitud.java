package org.rnpn.revfa.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalles_solicitudes")
public class DetalleSolicitud extends PanacheEntityBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_detalle_solicitud")
  public Long id;

  @Column(name = "valor")
  public String valor;

  @ManyToOne
  @JoinColumn(name = "id_campo")
  public Campo campos;

  @ManyToOne(optional = false)
  @JoinColumn(name = "id_solicitud", nullable = false)
  public Solicitud solicitud;
}
