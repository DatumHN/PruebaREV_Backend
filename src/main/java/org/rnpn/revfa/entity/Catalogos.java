package org.rnpn.revfa.entity;


import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "catalogos")
public class Catalogos extends PanacheEntityBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_catalogo")
  public Long id;

  @Column(name = "id_superior")
  public Long idSuperior;

  @Column(name = "nombre")
  public String nombre;


  @Column(name = "activo")
  public Character activo;

  @OneToMany(mappedBy = "catalogos", fetch = FetchType.EAGER)
  public List<Campo> campos;

  @OneToMany(mappedBy = "catalogos", fetch = FetchType.EAGER)
  public List<ValoresCatalogos> valoresCatalogos;

  @Override
  public String toString() {
    return "Catalogos{" + "id=" + id + ", idSuperior=" + idSuperior + ", nombre='" + nombre + '\''
        + ", activo=" + activo + ", campos=" + campos + ", valoresCatalogos=" + valoresCatalogos
        + '}';
  }

  public static Uni<Catalogos> findCatalogoHijo(Long id) {
    return find("idSuperior = ?1", id).firstResult();
  }

}
