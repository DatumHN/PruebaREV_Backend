package org.rnpn.revfa.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "valores_catalogos")
public class ValoresCatalogos {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_valor_catalogo")
  public Long id;

  @Column(name = "id_superior")
  public Long idSuperior;

  @Column(name = "valor")
  public String valor;

  @Column(name = "activo")
  public Character activo;

  @ManyToOne
  @JoinColumn(name = "id_catalogo")
  public Catalogos catalogos;

  @OneToMany(mappedBy = "valoresCatalogos", fetch = FetchType.EAGER)
  public List<AtributosValoresCatalogos> atributosValoresCatalogos;

  @OneToMany(mappedBy = "valoresCatalogos", fetch = FetchType.EAGER)
  public List<DetalleSolicitud> detalleSolicitudes;
}
