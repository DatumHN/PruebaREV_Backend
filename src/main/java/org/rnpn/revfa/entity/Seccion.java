package org.rnpn.revfa.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import org.rnpn.revfa.dto.CampoSeccionDTO;
import org.rnpn.revfa.dto.CatalogosDTO;
import org.rnpn.revfa.dto.SeccionesDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "secciones")
public class Seccion extends PanacheEntityBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_seccion")
  public Long id;

  @Column(name = "nombre")
  public String nombre;

  @Column(name = "secuencia")
  public Integer secuencia;

  @Column(name = "activo")
  public Character activo;

  @Column(name = "id_superior")
  public Long idSuperior;

  @Column(name = "ventana_emergente")
  public Character ventanaEmergente;

  @OneToMany(mappedBy = "seccion", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  public List<CampoSeccion> camposSecciones;

  @ManyToOne()
  @JoinColumn(name = "id_tipo_documento")
  public TipoDocumento tipoDocumento;

  @ManyToOne
  @JoinColumn(name = "id_etapa")
  public Etapa etapa;

  public static Uni<List<SeccionesDTO>> findByDocSecciones(String id) {
    return find(
        "SELECT s FROM Seccion s LEFT JOIN FETCH  s.camposSecciones WHERE s.idSuperior = ?1", id)
        .project(SeccionesDTO.class).list();
  }

  public static Uni<List<SeccionesDTO>> findSeccionesAll(String id, String rol) {

    CatalogosDTO catalogo = new CatalogosDTO();
    Uni<List<Seccion>> secciones = Seccion.find("SELECT DISTINCT s FROM Seccion s "
        + "LEFT JOIN FETCH s.camposSecciones cs "
        + "LEFT JOIN FETCH cs.rolesPermisosCamposSecciones rpcs " + "LEFT JOIN FETCH s.etapa e "
        + "LEFT JOIN FETCH e.rolesEtapas re " + "LEFT JOIN FETCH re.rol r "
        + "WHERE s.tipoDocumento.id = ?1 " + "AND (rpcs IS NULL OR LOWER(r.nombre) = LOWER(?2))  "
        + "AND LOWER(rpcs.rol.nombre) =  LOWER(?2)" + "ORDER BY s.secuencia ASC, cs.secuencia ASC",
        id, rol).list();

    return secciones.map(list -> {
      Map<Long, SeccionesDTO> mapaSecciones = new HashMap<>();
      List<SeccionesDTO> raices = new ArrayList<>();

      for (Seccion s : list) {
        SeccionesDTO dto = new SeccionesDTO(s);
        if (!dto.camposSecciones.isEmpty()) {
          for (CampoSeccionDTO campoSeccion : dto.camposSecciones) {
            if (campoSeccion.campo.catalogos != null) {
              catalogo.recursivo(campoSeccion.campo.catalogos.id).subscribe()
                  .with(resultado -> campoSeccion.campo.catalogos.subCatalogo = resultado);
            }
          }
        }
        mapaSecciones.put(dto.id, dto);
        dto.subSecciones = new ArrayList<>();
      }

      for (Seccion s : list) {
        SeccionesDTO dto = mapaSecciones.get(s.id);
        if (s.idSuperior == null || s.idSuperior == 0) {
          raices.add(dto); // raíz
        } else {
          SeccionesDTO padre = mapaSecciones.get(s.idSuperior);
          if (padre != null) {
            padre.subSecciones.add(dto); // hijos a padres
          }
        }
      }

      return raices;
    });
  }

  public static Uni<List<SeccionesDTO>> findDataAll(String id, String rol, String solicitud) {

    CatalogosDTO catalogo = new CatalogosDTO();
    Uni<List<Seccion>> secciones = Seccion.find("SELECT DISTINCT s FROM Seccion s "
        + "LEFT JOIN FETCH s.camposSecciones cs "
        + "LEFT JOIN FETCH cs.rolesPermisosCamposSecciones rpcs " + "LEFT JOIN FETCH rpcs.rol r "
        + "LEFT JOIN FETCH s.etapa e " + "WHERE s.tipoDocumento.id = ?1 "
        + "AND (rpcs IS NULL OR LOWER(r.nombre) = LOWER(?2)) "
        + "ORDER BY s.secuencia ASC, cs.secuencia ASC", id, rol).list();

    return secciones.map(list -> {
      Map<Long, SeccionesDTO> mapaSecciones = new HashMap<>();
      List<SeccionesDTO> raices = new ArrayList<>();

      for (Seccion s : list) {
        SeccionesDTO dto = new SeccionesDTO(s);
        if (!dto.camposSecciones.isEmpty()) {
          for (CampoSeccionDTO campoSeccion : dto.camposSecciones) {
            if (campoSeccion.campo.catalogos != null) {
              catalogo.recursivo(campoSeccion.campo.catalogos.id).subscribe()
                  .with(resultado -> campoSeccion.campo.catalogos.subCatalogo = resultado);
            }
          }
        }
        mapaSecciones.put(dto.id, dto);
        dto.subSecciones = new ArrayList<>();
      }

      for (Seccion s : list) {
        SeccionesDTO dto = mapaSecciones.get(s.id);
        if (s.idSuperior == null || s.idSuperior == 0) {
          raices.add(dto); // raíz
        } else {
          SeccionesDTO padre = mapaSecciones.get(s.idSuperior);
          if (padre != null) {
            padre.subSecciones.add(dto); // hijos a padres
          }
        }
      }

      return raices;
    });
  }

}
