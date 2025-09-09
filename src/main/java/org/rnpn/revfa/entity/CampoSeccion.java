package org.rnpn.revfa.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "pvt_campos_secciones")
public class CampoSeccion extends PanacheEntityBase {
  @Id
  @Column(name = "id_campo_seccion")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(name = "secuencia")
  public Integer secuencia;

  @Column(name = "activo")
  public Character activo;

  @OneToMany(mappedBy = "campoSeccion", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  public Set<RolPermisoCampoSeccion> rolesPermisosCamposSecciones;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_campo")
  public Campo campos;

  @ManyToOne
  @JoinColumn(name = "id_seccion")
  public Seccion seccion;
}
