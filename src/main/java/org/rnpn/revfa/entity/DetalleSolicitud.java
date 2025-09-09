package org.rnpn.revfa.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "detalles_solicitudes")
public class DetalleSolicitud extends PanacheEntityBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_detalle_solicitud")
  public Long id;

  @Column(name = "valor")
  public String valor;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_campo")
  public Campo campos;

  @Column(name = "campo_seccion")
  public Long campoSeccion;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "id_solicitud", nullable = false)
  public Solicitud solicitud;

  @ManyToOne(optional = true, fetch = FetchType.EAGER)
  @JoinColumn(name = "id_valor_catalogo", nullable = true)
  public ValoresCatalogos valoresCatalogos;
}
