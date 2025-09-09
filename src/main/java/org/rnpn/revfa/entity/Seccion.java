package org.rnpn.revfa.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import org.rnpn.revfa.dto.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

  public static Uni<List<StepperDTO>> findSteper(Long id) {
    return find("SELECT s FROM Seccion s WHERE s.tipoDocumento.id = ?1 AND s.idSuperior=?2 ", id, 0)
        .project(StepperDTO.class).list();
  }

  public static Uni<List<SeccionesDTO>> findSeccionesAll(String id, String rol) {

    CatalogosDTO catalogo = new CatalogosDTO();
    Uni<List<Seccion>> secciones =
        Seccion.find("SELECT DISTINCT s FROM Seccion s " + "LEFT JOIN FETCH s.camposSecciones cs "
            + "LEFT JOIN FETCH s.etapa e " + "LEFT JOIN FETCH e.rolesEtapas re "
            + "WHERE s.tipoDocumento.id = ?1 " + "AND LOWER(re.rol.nombre) = LOWER(?2) "
            + "ORDER BY s.secuencia ASC, cs.secuencia ASC", id, rol).list();

    return secciones.map(list -> {
      Map<Long, SeccionesDTO> mapaSecciones = new HashMap<>();
      List<SeccionesDTO> raices = new ArrayList<>();

      for (Seccion s : list) {
        SeccionesDTO dto = new SeccionesDTO(s);

        if (!dto.camposSecciones.isEmpty()) {

          // Filtramos aquí los campos sin el rol especificado
          dto.camposSecciones = dto.camposSecciones.stream().map(campo -> {
            // Filtrar roles dentro de cada campo para que solo queden los que coinciden con el rol
            // dado
            if (campo.rolesPermisos != null) {
              campo.rolesPermisos = campo.rolesPermisos.stream()
                  .filter(rp -> rp.nombreRol != null && rp.nombreRol != null
                      && rp.nombreRol.trim().equalsIgnoreCase(rol.trim()))
                  .collect(Collectors.toList());
            }
            return campo;
          })
              // Mantener solo los campos que quedaron con al menos un rol válido
              .filter(campo -> campo.rolesPermisos != null && !campo.rolesPermisos.isEmpty())
              .collect(Collectors.toList());

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

  public static Uni<List<SeccionesCompDTO>> findDataAll(String id, String rol, String solicitud) {


    CatalogosDTO catalogo = new CatalogosDTO();
    Uni<List<Seccion>> secciones =
        Seccion.find("SELECT DISTINCT s FROM Seccion s " + "LEFT JOIN FETCH s.camposSecciones cs "
            + "LEFT JOIN FETCH s.etapa e " + "LEFT JOIN FETCH e.rolesEtapas re "
            + "WHERE s.tipoDocumento.id = ?1 " + "AND LOWER(re.rol.nombre) = LOWER(?2) "
            + "ORDER BY s.secuencia ASC, cs.secuencia ASC", id, rol).list();

    return secciones.map(list -> {
      Map<Long, SeccionesCompDTO> mapaSecciones = new HashMap<>();
      List<SeccionesCompDTO> raices = new ArrayList<>();

      for (Seccion s : list) {
        SeccionesCompDTO dto = new SeccionesCompDTO(s);

        if (!dto.camposSecciones.isEmpty()) {

          // Filtramos aquí los campos sin el rol especificado
          dto.camposSecciones = dto.camposSecciones.stream().map(campo -> {
            // Filtrar roles dentro de cada campo para que solo queden los que coinciden con el rol
            // dado
            if (campo.rolesPermisos != null) {
              campo.rolesPermisos = campo.rolesPermisos.stream()
                  .filter(rp -> rp.nombreRol != null && rp.nombreRol != null
                      && rp.nombreRol.trim().equalsIgnoreCase(rol.trim()))
                  .collect(Collectors.toList());
            }
            if (campo.campo != null && campo.campo.detalleSolicitud != null) {
              campo.campo.detalleSolicitud = campo.campo.detalleSolicitud.stream()
                  .filter(ds -> ds.solicitud != null && ds.solicitud.id != null
                      && ds.solicitud.id.toString().equals(solicitud) && ds.campoSeccion != null
                      && ds.campoSeccion.equals(campo.id))
                  .collect(Collectors.toList());
            }


            return campo;
          })
              // Mantener solo los campos que quedaron con al menos un rol válido
              .filter(campo -> campo.rolesPermisos != null && !campo.rolesPermisos.isEmpty())
              .collect(Collectors.toList());

          for (CampoSeccionCompDTO campoSeccion : dto.camposSecciones) {
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
        SeccionesCompDTO dto = mapaSecciones.get(s.id);
        if (s.idSuperior == null || s.idSuperior == 0) {
          raices.add(dto); // raíz
        } else {
          SeccionesCompDTO padre = mapaSecciones.get(s.idSuperior);
          if (padre != null) {
            padre.subSecciones.add(dto); // hijos a padres
          }
        }
      }

      return raices;
    });
  }

  public static Uni<List<SeccionesDTO>> findSeccionesPrueba(String id, String rol) {
    String query = "SELECT DISTINCT s FROM Seccion s " + "LEFT JOIN FETCH s.camposSecciones cs "
        + "LEFT JOIN FETCH s.etapa e " + "LEFT JOIN FETCH e.rolesEtapas re "
        + "WHERE s.tipoDocumento.id = ?1 " + "AND LOWER(re.rol.nombre) = LOWER(?2) "
        + "ORDER BY s.secuencia ASC, cs.secuencia ASC";

    Uni<List<Seccion>> secciones = Seccion.<Seccion>find(query, id, rol).list();
    return secciones.map(list -> construirArbolSecciones(list, rol));
  }

  private static List<SeccionesDTO> construirArbolSecciones(List<Seccion> list, String rol) {
    Map<Long, SeccionesDTO> mapaSecciones = new HashMap<>();
    List<SeccionesDTO> raices = new ArrayList<>();

    for (Seccion s : list) {
      SeccionesDTO dto = new SeccionesDTO(s);
      dto.camposSecciones = filtrarCamposPorRol(dto.camposSecciones, rol);
      mapaSecciones.put(dto.id, dto);
      dto.subSecciones = new ArrayList<>();
    }

    for (Seccion s : list) {
      SeccionesDTO dto = mapaSecciones.get(s.id);
      if (s.idSuperior == null || s.idSuperior == 0) {
        raices.add(dto);
      } else {
        SeccionesDTO padre = mapaSecciones.get(s.idSuperior);
        if (padre != null) {
          padre.subSecciones.add(dto);
        }
      }
    }
    return raices;
  }

  private static List<CampoSeccionDTO> filtrarCamposPorRol(List<CampoSeccionDTO> campos,
      String rol) {
    if (campos == null)
      return new ArrayList<>();
    return campos.stream().peek(campo -> {
      if (campo.rolesPermisos != null) {
        campo.rolesPermisos = campo.rolesPermisos.stream()
            .filter(rp -> rp.nombreRol != null && rp.nombreRol.trim().equalsIgnoreCase(rol.trim()))
            .collect(Collectors.toList());
      }
    }).filter(campo -> campo.rolesPermisos != null && !campo.rolesPermisos.isEmpty())
        .collect(Collectors.toList());
  }
}
