package org.rnpn.revfa.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import org.rnpn.revfa.dto.CampoDTO;

import java.sql.Clob;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "cat_campos")
public class Campo extends PanacheEntityBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_campo")
  public Long id;

  @Column(name = "nombre")
  public String nombre;

  @Column(name = "tipo")
  public String tipo;

  @Column(name = "valores_posibles")
  public Clob valoresPosibles;

  @Column(name = "valor_predeterminado")
  public String valorPredeterminado;

  @Column(name = "obligatorio")
  public Character obligatorio;

  @Column(name = "ancho")
  public String ancho;

  @Column(name = "ejemplo")
  public String ejemplo;

  @Column(name = "ayuda")
  public String ayuda;

  @Column(name = "activo")
  public Character activo;

  @Column(name = "mascara")
  public String mascara;

  @Column(name = "validacion_compleja")
  public String validCompleja;

  @Column(name = "busqueda")
  public Character busqueda;

  @Column(name = "longitud_minima")
  public Integer lonMinima;

  @Column(name = "longitud_maxima")
  public Integer lonMaxima;

  @OneToMany(mappedBy = "campo", fetch = FetchType.EAGER)
  public Set<CampoSeccion> camposSecciones;

  @OneToMany(mappedBy = "campos", fetch = FetchType.EAGER)
  public List<DetalleSolicitud> detallesSolicitudes;

  @ManyToOne
  @JoinColumn(name = "id_catalogo")
  public Catalogos catalogos;


  /*
   * public static Uni<List<CampoDTO>> findByIdSeccion(String id) { return find("SELECT cp " +
   * "FROM Campo cp " + "JOIN cp.pvtCamposSecciones pcs " + "JOIN pcs.seccion s " +
   * "WHERE s.id = ?1 ORDER BY pcs.secuencia ASC", id) .project(CampoDTO.class).list(); }
   */

  public static Uni<List<CampoDTO>> seleccionarTodo(String id) {
    return find(
        "SELECT cp FROM Campo cp JOIN CampoSeccion cs ON cs.campo.id = cp.id WHERE cs.seccion.id = ?1",
        id).project(CampoDTO.class).list();
  }
}
