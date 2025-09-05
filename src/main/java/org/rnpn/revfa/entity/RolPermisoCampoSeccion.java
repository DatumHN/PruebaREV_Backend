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
@Table(name = "roles_permisos_campos_secciones")
public class RolPermisoCampoSeccion extends PanacheEntityBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_rol_permiso_campo_seccion")
  public Long id;

  @ManyToOne
  @JoinColumn(name = "id_rol")
  public Rol rol;

  @JoinColumn(name = "permiso")
  public Character permiso;

  @ManyToOne
  @JoinColumn(name = "id_campo_seccion")
  public CampoSeccion campoSeccion;

  @Column(name = "activo")
  public Character activo;
}
