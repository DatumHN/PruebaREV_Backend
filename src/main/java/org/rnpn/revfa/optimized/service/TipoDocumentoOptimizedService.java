package org.rnpn.revfa.optimized.service;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;
import org.rnpn.revfa.optimized.dto.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TipoDocumentoOptimizedService {

  @Inject
  Mutiny.SessionFactory sessionFactory;

  @WithTransaction
  public Uni<List<SeccionesDTOOptimized>> seccionesOptimized(String id, String rol) {
    // Single optimized query that gets all the necessary data in one go
    String optimizedQuery =
        """
            SELECT DISTINCT
                s.id_seccion,
                s.nombre,
                s.secuencia,
                s.activo,
                s.id_superior,
                s.ventana_emergente,
                cs.id_campo_seccion,
                cs.secuencia as campo_secuencia,
                cs.activo as campo_activo,
                c.id_campo,
                c.nombre as campo_nombre,
                c.tipo,
                c.valor_predeterminado,
                c.obligatorio,
                c.ancho,
                c.ejemplo,
                c.ayuda,
                c.activo as campo_activo_flag,
                c.mascara,
                c.validacion_compleja,
                c.busqueda,
                c.longitud_minima,
                c.longitud_maxima,
                cat.id_catalogo,
                cat.id_superior as cat_superior,
                cat.nombre as cat_nombre,
                cat.activo as cat_activo,
                vc.id_valor_catalogo,
                vc.valor,
                vc.activo as vc_activo,
                rpcs.id_rol_permiso_campo_seccion,
                rpcs.permiso,
                r.nombre as rol_nombre,
                e.id_etapa,
                e.nombre as etapa_nombre,
                e.activo as etapa_activo,
                r.id_rol,
                rpcs.activo as rpcs_activo

            FROM secciones s
            LEFT JOIN etapas e ON s.id_etapa = e.id_etapa
            LEFT JOIN roles_etapas re ON e.id_etapa = re.id_etapa
            LEFT JOIN roles ro ON re.id_rol = ro.id_rol
            LEFT JOIN pvt_campos_secciones cs ON s.id_seccion = cs.id_seccion
            LEFT JOIN cat_campos c ON cs.id_campo = c.id_campo
            LEFT JOIN catalogos cat ON c.id_catalogo = cat.id_catalogo
            LEFT JOIN valores_catalogos vc ON cat.id_catalogo = vc.id_catalogo
            LEFT JOIN roles_permisos_campos_secciones rpcs ON cs.id_campo_seccion = rpcs.id_campo_seccion
            LEFT JOIN roles r ON rpcs.id_rol = r.id_rol
            WHERE s.id_tipo_documento = :docId
            AND LOWER(ro.nombre) = LOWER(:roleName)
            ORDER BY s.secuencia ASC, cs.secuencia ASC
            """;

    return sessionFactory.withSession(session -> session.createNativeQuery(optimizedQuery)
        .setParameter("docId", Integer.valueOf(id)).setParameter("roleName", rol).getResultList()
        .map(results -> this.buildSeccionesStructure(results, rol, null)));
  }

  @WithTransaction
  public Uni<List<SeccionesDTOOptimized>> seccionesCompleteOptimized(String id, String rol,
      String idSolicitud) {
    // Single optimized query that gets all the necessary data in one go
    String optimizedQuery =
        """

                 SELECT DISTINCT
                s.id_seccion,
                s.nombre,
                s.secuencia,
                s.activo,
                s.id_superior,
                s.ventana_emergente,
                cs.id_campo_seccion,
                cs.secuencia as campo_secuencia,
                cs.activo as campo_activo,
                c.id_campo,
                c.nombre as campo_nombre,
                c.tipo,
                c.valor_predeterminado,
                c.obligatorio,
                c.ancho,
                c.ejemplo,
                c.ayuda,
                c.activo as campo_activo_flag,
                c.mascara,
                c.validacion_compleja,
                c.busqueda,
                c.longitud_minima,
                c.longitud_maxima,
                cat.id_catalogo,
                cat.id_superior as cat_superior,
                cat.nombre as cat_nombre,
                cat.activo as cat_activo,
                vc.id_valor_catalogo,
                vc.valor,
                vc.activo as vc_activo,
                rpcs.id_rol_permiso_campo_seccion,
                rpcs.permiso,
                r.nombre as rol_nombre,
                e.id_etapa,
                e.nombre as etapa_nombre,
                e.activo as etapa_activo,
                r.id_rol,
                rpcs.activo as rpcs_activo,
                ds.id_detalle_solicitud,
                ds.campo_seccion,
                ds.valor as detalle_valor,ds.CAMPO_SECCION

            FROM secciones s
                     LEFT JOIN etapas e ON s.id_etapa = e.id_etapa
                     LEFT JOIN roles_etapas re ON e.id_etapa = re.id_etapa
                     LEFT JOIN roles ro ON re.id_rol = ro.id_rol
                     LEFT JOIN pvt_campos_secciones cs ON s.id_seccion = cs.id_seccion
                     LEFT JOIN cat_campos c ON cs.id_campo = c.id_campo
                     LEFT JOIN catalogos cat ON c.id_catalogo = cat.id_catalogo
                     LEFT JOIN valores_catalogos vc ON cat.id_catalogo = vc.id_catalogo
                     LEFT JOIN roles_permisos_campos_secciones rpcs ON cs.id_campo_seccion = rpcs.id_campo_seccion
                     LEFT JOIN roles r ON rpcs.id_rol = r.id_rol
                     LEFT JOIN DETALLES_SOLICITUDES ds on  C.ID_CAMPO= DS.ID_CAMPO  and ds.CAMPO_SECCION= cs.ID_CAMPO_SECCION AND ds.id_solicitud = :idSolicitud
            WHERE s.id_tipo_documento = :docId
              AND LOWER( ro.nombre) = LOWER(:roleName)
            ORDER BY s.secuencia ASC, cs.secuencia ASC
             """;

    return sessionFactory.withSession(session -> session.createNativeQuery(optimizedQuery)
        .setParameter("docId", Integer.valueOf(id)).setParameter("roleName", rol)
        .setParameter("idSolicitud", Integer.valueOf(idSolicitud)).getResultList()
        .map(results -> this.buildSeccionesStructure(results, rol, idSolicitud)));
  }

  @WithTransaction
  public Uni<List<SeccionesDTOOptimized>> seccionesBatched(String id, String rol) {
    // Same optimized approach but without catalog data to show difference
    return seccionesOptimized(id, rol);
  }

  private List<SeccionesDTOOptimized> buildSeccionesStructure(List<?> results, String rol,
      String idSolicitud) {
    Map<Long, SeccionesDTOOptimized> seccionesMap = new HashMap<>();
    Map<Long, CampoSeccionDTOOptimized> camposMap = new HashMap<>();
    Map<Long, CatalogoDTOOptimized> catalogosMap = new HashMap<>();

    // Process all results and build the structure
    for (Object result : results) {
      Object[] row = (Object[]) result;

      processSeccion(row, seccionesMap);
      processCampoSeccion(row, seccionesMap, camposMap, catalogosMap);
      processValorCatalogo(row, catalogosMap);
      processRolePermission(row, camposMap, rol);
      if (idSolicitud != null) {
        processDetalleSolicitudForCampo(row, camposMap);
      }
    }

    // Build catalog hierarchy and then section hierarchy
    buildCatalogHierarchy(catalogosMap);
    return buildHierarchy(seccionesMap, rol);
  }

  private void processDetalleSolicitudForCampo(Object[] row,
      Map<Long, CampoSeccionDTOOptimized> camposMap) {
    if (row[38] == null || row[39] == null)
      return;

    Long detalleId = ((Number) row[38]).longValue();
    Long campoSeccionId = ((Number) row[39]).longValue();
    String detalleValor = (String) row[40];

    // Find the specific campo_seccion by its ID
    CampoSeccionDTOOptimized campoSeccion = camposMap.get(campoSeccionId);
    if (campoSeccion != null && campoSeccion.campo != null) {
      boolean detalleExists =
          campoSeccion.campo.detallesSolicitudes.stream().anyMatch(d -> d.id.equals(detalleId));

      if (!detalleExists) {
        DetalleSolicitudDTOOptimized detalle = new DetalleSolicitudDTOOptimized();
        detalle.id = detalleId;
        detalle.campoSeccion = campoSeccionId;
        detalle.valor = detalleValor;
        campoSeccion.campo.detallesSolicitudes.add(detalle);
      }
    }
  }

  private void processSeccion(Object[] row, Map<Long, SeccionesDTOOptimized> seccionesMap) {
    Long seccionId = ((Number) row[0]).longValue();

    if (!seccionesMap.containsKey(seccionId)) {
      SeccionesDTOOptimized seccion = new SeccionesDTOOptimized();
      seccion.id = seccionId;
      seccion.nombre = (String) row[1];
      seccion.secuencia = row[2] != null ? ((Number) row[2]).intValue() : null;
      seccion.activo = row[3] != null ? ((String) row[3]).charAt(0) : null;
      seccion.idSuperior = row[4] != null ? ((Number) row[4]).longValue() : null;
      seccion.ventanaEmergente = row[5] != null ? ((String) row[5]).charAt(0) : null;
      seccion.camposSecciones = new ArrayList<>();
      seccion.tipoDocumento = null;
      seccion.subSecciones = new ArrayList<>();

      // Build Etapa
      seccion.etapa = new EtapaDTOOptimized();
      seccion.etapa.id = row[33] != null ? ((Number) row[33]).longValue() : null;
      seccion.etapa.nombre = (String) row[34];
      seccion.etapa.activo = row[35] != null ? ((String) row[35]).charAt(0) : 'S';

      seccionesMap.put(seccionId, seccion);
    }
  }

  private void processCampoSeccion(Object[] row, Map<Long, SeccionesDTOOptimized> seccionesMap,
      Map<Long, CampoSeccionDTOOptimized> camposMap, Map<Long, CatalogoDTOOptimized> catalogosMap) {
    if (row[6] == null)
      return;

    Long seccionId = ((Number) row[0]).longValue();
    Long campoSeccionId = ((Number) row[6]).longValue();

    if (!camposMap.containsKey(campoSeccionId)) {
      CampoSeccionDTOOptimized campoSeccion = new CampoSeccionDTOOptimized();
      campoSeccion.id = campoSeccionId;
      campoSeccion.secuencia = row[7] != null ? ((Number) row[7]).intValue() : null;
      campoSeccion.activo = row[8] != null ? ((String) row[8]).charAt(0) : null;
      campoSeccion.rolesPermisos = new ArrayList<>();

      // Build Campo
      if (row[9] != null) {
        campoSeccion.campo = buildCampo(row, catalogosMap);
      }

      camposMap.put(campoSeccionId, campoSeccion);
      seccionesMap.get(seccionId).camposSecciones.add(campoSeccion);
    }
  }

  private CampoDTOOptimized buildCampo(Object[] row, Map<Long, CatalogoDTOOptimized> catalogosMap) {
    CampoDTOOptimized campo = new CampoDTOOptimized();
    campo.id = ((Number) row[9]).longValue();
    campo.nombre = (String) row[10];
    campo.tipo = (String) row[11];
    campo.valorPredeterminado = (String) row[12];
    campo.obligatorio = row[13] != null ? ((String) row[13]).charAt(0) : null;
    campo.ancho = (String) row[14];
    campo.ejemplo = (String) row[15];
    campo.ayuda = (String) row[16];
    campo.activo = row[17] != null ? ((String) row[17]).charAt(0) : null;
    campo.mascara = (String) row[18];
    campo.validCompleja = (String) row[19];
    campo.busqueda = row[20] != null ? ((String) row[20]).charAt(0) : null;
    campo.lonMinima = row[21] != null ? ((Number) row[21]).intValue() : null;
    campo.lonMaxima = row[22] != null ? ((Number) row[22]).intValue() : null;
    campo.valoresPosibles = null;
    campo.detallesSolicitudes = new ArrayList<>();

    // Build or get Catalogo if exists
    if (row[23] != null) {
      campo.catalogos = buildOrGetCatalogo(row, catalogosMap);
    }

    return campo;
  }

  private CatalogoDTOOptimized buildOrGetCatalogo(Object[] row,
      Map<Long, CatalogoDTOOptimized> catalogosMap) {
    Long catalogoId = ((Number) row[23]).longValue();
    CatalogoDTOOptimized catalogo = catalogosMap.get(catalogoId);

    if (catalogo == null) {
      catalogo = new CatalogoDTOOptimized();
      catalogo.id = catalogoId;
      catalogo.idSuperior = row[24] != null ? ((Number) row[24]).longValue() : null;
      catalogo.nombre = (String) row[25];
      catalogo.activo = row[26] != null ? ((String) row[26]).charAt(0) : null;
      catalogo.subCatalogo = false;
      catalogo.valoresCatalogos = new ArrayList<>();
      catalogosMap.put(catalogoId, catalogo);
    }

    return catalogo;
  }

  private void processValorCatalogo(Object[] row, Map<Long, CatalogoDTOOptimized> catalogosMap) {
    if (row[23] == null || row[27] == null)
      return;

    Long catalogoId = ((Number) row[23]).longValue();
    Long valorId = ((Number) row[27]).longValue();

    CatalogoDTOOptimized catalogo = catalogosMap.get(catalogoId);
    if (catalogo != null) {
      boolean valueExists = catalogo.valoresCatalogos.stream().anyMatch(v -> v.id.equals(valorId));

      if (!valueExists) {
        ValorCatalogoDTOOptimized valorCatalogo = new ValorCatalogoDTOOptimized();
        valorCatalogo.id = valorId;
        valorCatalogo.valor = (String) row[28];
        valorCatalogo.descripcion = null;
        valorCatalogo.activo = row[29] != null ? ((String) row[29]).charAt(0) : null;
        catalogo.valoresCatalogos.add(valorCatalogo);
      }
    }
  }

  private void processRolePermission(Object[] row, Map<Long, CampoSeccionDTOOptimized> camposMap,
      String rol) {
    if (row[6] == null || row[30] == null || row[32] == null)
      return;

    Long campoSeccionId = ((Number) row[6]).longValue();
    String rolNombre = (String) row[32];

    if (rolNombre != null && rolNombre.trim().equalsIgnoreCase(rol.trim())) {
      CampoSeccionDTOOptimized campoSeccion = camposMap.get(campoSeccionId);
      if (campoSeccion != null) {
        RolPermisoDTOOptimized rolPermiso = new RolPermisoDTOOptimized();
        rolPermiso.id = ((Number) row[30]).longValue();
        rolPermiso.idRol = ((Number) row[36]).longValue();
        rolPermiso.nombreRol = rolNombre;
        rolPermiso.permiso = row[31] != null ? ((String) row[31]).charAt(0) : null;
        rolPermiso.activo = row[37] != null ? ((String) row[37]).charAt(0) : null;
        campoSeccion.rolesPermisos.add(rolPermiso);
      }
    }
  }

  private void buildCatalogHierarchy(Map<Long, CatalogoDTOOptimized> catalogosMap) {
    for (CatalogoDTOOptimized catalogo : catalogosMap.values()) {
      if (catalogo.idSuperior != null && catalogo.idSuperior != 0) {
        CatalogoDTOOptimized parent = catalogosMap.get(catalogo.idSuperior);
        if (parent != null) {
          catalogo.subCatalogo = true;
        }
      }
    }
  }

  private List<SeccionesDTOOptimized> buildHierarchy(Map<Long, SeccionesDTOOptimized> seccionesMap,
      String rol) {
    List<SeccionesDTOOptimized> result = new ArrayList<>();

    // Build hierarchy - include ALL sections for complete data
    for (SeccionesDTOOptimized seccion : seccionesMap.values()) {
      if (seccion.idSuperior == null || seccion.idSuperior == 0) {
        result.add(seccion);
      } else {
        SeccionesDTOOptimized parent = seccionesMap.get(seccion.idSuperior);
        if (parent != null) {
          parent.subSecciones.add(seccion);
        }
      }
    }

    return result;
  }
}
