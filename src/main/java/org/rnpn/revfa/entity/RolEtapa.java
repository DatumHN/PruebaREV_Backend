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
@Table(name = "roles_etapas")
public class RolEtapa extends PanacheEntityBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_rol_etapa")
  public Long id;

  @Column(name = "activo")
  public Character activo;

  @ManyToOne
  @JoinColumn(name = "id_rol")
  public Rol rol;

  @ManyToOne
  @JoinColumn(name = "id_etapa")
  public Etapa etapa;
}
