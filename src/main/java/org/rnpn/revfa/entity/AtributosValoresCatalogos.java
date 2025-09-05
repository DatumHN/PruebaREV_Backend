package org.rnpn.revfa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "atributos_valores_catalogos")
public class AtributosValoresCatalogos {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_atributo_valor_catalogo")
  public Long id;

  @ManyToOne
  @JoinColumn(name = "id_valor_catalogo")
  public ValoresCatalogos valoresCatalogos;

  @Column(name = "nombre")
  public String nombre;

  @Column(name = "valor")
  public String valor;

  @Column(name = "activo")
  public Character activo;

}
