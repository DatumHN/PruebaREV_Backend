package org.rnpn.revfa.entity;

import java.util.Set;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "etapas")
public class Etapa extends PanacheEntityBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_etapa")
  public Long id;

  @Column(name = "nombre")
  public String nombre;

  @Column(name = "activo")
  public Character activo;

  @OneToMany(mappedBy = "etapa", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  public Set<RolEtapa> rolesEtapas;

  @OneToMany(mappedBy = "etapa", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  public Set<Seccion> secciones;
}
