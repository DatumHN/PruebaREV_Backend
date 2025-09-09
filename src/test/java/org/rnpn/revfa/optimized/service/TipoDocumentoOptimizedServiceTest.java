package org.rnpn.revfa.optimized.service;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rnpn.revfa.optimized.dto.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.function.Function;

@QuarkusTest
class TipoDocumentoOptimizedServiceTest {

  @Inject
  TipoDocumentoOptimizedService tipoDocumentoOptimizedService;

  @InjectMock
  Mutiny.SessionFactory sessionFactory;

  private Mutiny.Session mockSession;
  private Mutiny.Query mockQuery;

  @BeforeEach
  void setup() {
    mockSession = Mockito.mock(Mutiny.Session.class);
    mockQuery = Mockito.mock(Mutiny.Query.class);
  }

  @Test
  @DisplayName("Should return sections with campos and catalogs")
  void testSeccionesOptimized_WithCompleteData() {
    // Mock data simulating a complex query result
    Object[] mockRow = createMockCompleteRow();
    List<Object> mockResults = Arrays.asList(mockRow);

    when(sessionFactory.withSession(any())).thenAnswer(invocation -> {
      Function<Mutiny.Session, Uni<?>> function = invocation.getArgument(0);
      return function.apply(mockSession);
    });

    when(mockSession.createNativeQuery(anyString())).thenReturn(mockQuery);
    when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
    when(mockQuery.getResultList()).thenReturn(Uni.createFrom().item(mockResults));

    UniAssertSubscriber<List<SeccionesDTOOptimized>> subscriber = tipoDocumentoOptimizedService
        .seccionesOptimized("1", "ADMIN").subscribe().withSubscriber(UniAssertSubscriber.create());

    List<SeccionesDTOOptimized> result = subscriber.awaitItem().getItem();

    assertNotNull(result);
    assertEquals(1, result.size());

    SeccionesDTOOptimized seccion = result.get(0);
    assertEquals(1L, seccion.id);
    assertEquals("Test Seccion", seccion.nombre);
    assertEquals(1, seccion.secuencia);
    assertEquals('S', seccion.activo);
    assertEquals('N', seccion.ventanaEmergente);

    // Verify Etapa
    assertNotNull(seccion.etapa);
    assertEquals(10L, seccion.etapa.id);
    assertEquals("Test Etapa", seccion.etapa.nombre);
    assertEquals('S', seccion.etapa.activo);

    // Verify Campos Secciones
    assertNotNull(seccion.camposSecciones);
    assertEquals(1, seccion.camposSecciones.size());

    CampoSeccionDTOOptimized campoSeccion = seccion.camposSecciones.get(0);
    assertEquals(100L, campoSeccion.id);
    assertEquals(1, campoSeccion.secuencia);
    assertEquals('S', campoSeccion.activo);

    // Verify Campo
    assertNotNull(campoSeccion.campo);
    CampoDTOOptimized campo = campoSeccion.campo;
    assertEquals(50L, campo.id);
    assertEquals("Test Campo", campo.nombre);
    assertEquals("TEXT", campo.tipo);
    assertEquals("Default Value", campo.valorPredeterminado);
    assertEquals('S', campo.obligatorio);

    // Verify Catalogo
    assertNotNull(campo.catalogos);
    CatalogoDTOOptimized catalogo = campo.catalogos;
    assertEquals(200L, catalogo.id);
    assertEquals("Test Catalogo", catalogo.nombre);
    assertEquals('S', catalogo.activo);
    assertFalse(catalogo.subCatalogo);

    // Verify Valores Catalogo
    assertNotNull(catalogo.valoresCatalogos);
    assertEquals(1, catalogo.valoresCatalogos.size());
    ValorCatalogoDTOOptimized valor = catalogo.valoresCatalogos.get(0);
    assertEquals(300L, valor.id);
    assertEquals("Test Valor", valor.valor);
    assertEquals('S', valor.activo);

    // Verify Role Permission
    assertNotNull(campoSeccion.rolesPermisos);
    assertEquals(1, campoSeccion.rolesPermisos.size());
    RolPermisoDTOOptimized rolPermiso = campoSeccion.rolesPermisos.get(0);
    assertEquals(400L, rolPermiso.id);
    assertEquals(500L, rolPermiso.idRol);
    assertEquals("ADMIN", rolPermiso.nombreRol);
    assertEquals('E', rolPermiso.permiso);
    assertEquals('S', rolPermiso.activo);
  }

  @Test
  @DisplayName("Should return sections complete with detalle solicitud data")
  void testSeccionesCompleteOptimized_WithDetalleData() {
    Object[] mockRow = createMockCompleteRowWithDetalle();
    List<Object> mockResults = Arrays.asList(mockRow);

    when(sessionFactory.withSession(any())).thenAnswer(invocation -> {
      Function<Mutiny.Session, Uni<?>> function = invocation.getArgument(0);
      return function.apply(mockSession);
    });

    when(mockSession.createNativeQuery(anyString())).thenReturn(mockQuery);
    when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
    when(mockQuery.getResultList()).thenReturn(Uni.createFrom().item(mockResults));

    UniAssertSubscriber<List<SeccionesDTOOptimized>> subscriber =
        tipoDocumentoOptimizedService.seccionesCompleteOptimized("1", "ADMIN", "123").subscribe()
            .withSubscriber(UniAssertSubscriber.create());

    List<SeccionesDTOOptimized> result = subscriber.awaitItem().getItem();

    assertNotNull(result);
    assertEquals(1, result.size());

    SeccionesDTOOptimized seccion = result.get(0);
    CampoSeccionDTOOptimized campoSeccion = seccion.camposSecciones.get(0);
    CampoDTOOptimized campo = campoSeccion.campo;

    // Verify detalle solicitud was populated
    assertNotNull(campo.detallesSolicitudes);
    assertEquals(1, campo.detallesSolicitudes.size());

    DetalleSolicitudDTOOptimized detalle = campo.detallesSolicitudes.get(0);
    assertEquals(999L, detalle.id);
    assertEquals(100L, detalle.campoSeccion);
    assertEquals("Detalle Test Value", detalle.valor);
  }

  @Test
  @DisplayName("Should return empty list when no data exists")
  void testSeccionesOptimized_WithEmptyResults() {
    List<Object> emptyResults = Collections.emptyList();

    when(sessionFactory.withSession(any())).thenAnswer(invocation -> {
      Function<Mutiny.Session, Uni<?>> function = invocation.getArgument(0);
      return function.apply(mockSession);
    });

    when(mockSession.createNativeQuery(anyString())).thenReturn(mockQuery);
    when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
    when(mockQuery.getResultList()).thenReturn(Uni.createFrom().item(emptyResults));

    UniAssertSubscriber<List<SeccionesDTOOptimized>> subscriber = tipoDocumentoOptimizedService
        .seccionesOptimized("1", "ADMIN").subscribe().withSubscriber(UniAssertSubscriber.create());

    List<SeccionesDTOOptimized> result = subscriber.awaitItem().getItem();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should build hierarchy with parent-child sections")
  void testSeccionesOptimized_WithHierarchy() {
    Object[] parentRow = createMockParentSectionRow();
    Object[] childRow = createMockChildSectionRow();
    List<Object> mockResults = Arrays.asList(parentRow, childRow);

    when(sessionFactory.withSession(any())).thenAnswer(invocation -> {
      Function<Mutiny.Session, Uni<?>> function = invocation.getArgument(0);
      return function.apply(mockSession);
    });

    when(mockSession.createNativeQuery(anyString())).thenReturn(mockQuery);
    when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
    when(mockQuery.getResultList()).thenReturn(Uni.createFrom().item(mockResults));

    UniAssertSubscriber<List<SeccionesDTOOptimized>> subscriber = tipoDocumentoOptimizedService
        .seccionesOptimized("1", "ADMIN").subscribe().withSubscriber(UniAssertSubscriber.create());

    List<SeccionesDTOOptimized> result = subscriber.awaitItem().getItem();

    assertNotNull(result);
    assertEquals(1, result.size()); // Only parent sections in root level

    SeccionesDTOOptimized parentSeccion = result.get(0);
    assertEquals(1L, parentSeccion.id);
    assertEquals("Parent Section", parentSeccion.nombre);

    // Verify child section is in subsecciones
    assertNotNull(parentSeccion.subSecciones);
    assertEquals(1, parentSeccion.subSecciones.size());

    SeccionesDTOOptimized childSeccion = parentSeccion.subSecciones.get(0);
    assertEquals(2L, childSeccion.id);
    assertEquals("Child Section", childSeccion.nombre);
    assertEquals(1L, childSeccion.idSuperior);
  }

  @Test
  @DisplayName("Should handle catalog hierarchy properly")
  void testSeccionesOptimized_WithCatalogHierarchy() {
    Object[] mockRowParentCatalog = createMockRowWithParentCatalog();
    Object[] mockRowChildCatalog = createMockRowWithChildCatalog();
    List<Object> mockResults = Arrays.asList(mockRowParentCatalog, mockRowChildCatalog);

    when(sessionFactory.withSession(any())).thenAnswer(invocation -> {
      Function<Mutiny.Session, Uni<?>> function = invocation.getArgument(0);
      return function.apply(mockSession);
    });

    when(mockSession.createNativeQuery(anyString())).thenReturn(mockQuery);
    when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
    when(mockQuery.getResultList()).thenReturn(Uni.createFrom().item(mockResults));

    UniAssertSubscriber<List<SeccionesDTOOptimized>> subscriber = tipoDocumentoOptimizedService
        .seccionesOptimized("1", "ADMIN").subscribe().withSubscriber(UniAssertSubscriber.create());

    List<SeccionesDTOOptimized> result = subscriber.awaitItem().getItem();

    assertNotNull(result);
    assertEquals(1, result.size());

    SeccionesDTOOptimized seccion = result.get(0);
    CampoSeccionDTOOptimized campoSeccion = seccion.camposSecciones.get(0);
    CampoDTOOptimized campo = campoSeccion.campo;
    CatalogoDTOOptimized catalogo = campo.catalogos;

    // Parent catalog should not have subCatalogo flag
    assertEquals(200L, catalogo.id);
    assertFalse(catalogo.subCatalogo);
  }

  @Test
  @DisplayName("Should call seccionesOptimized for batched method")
  void testSeccionesBatched() {
    List<Object> mockResults = Collections.emptyList();

    when(sessionFactory.withSession(any())).thenAnswer(invocation -> {
      Function<Mutiny.Session, Uni<?>> function = invocation.getArgument(0);
      return function.apply(mockSession);
    });

    when(mockSession.createNativeQuery(anyString())).thenReturn(mockQuery);
    when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
    when(mockQuery.getResultList()).thenReturn(Uni.createFrom().item(mockResults));

    UniAssertSubscriber<List<SeccionesDTOOptimized>> subscriber = tipoDocumentoOptimizedService
        .seccionesBatched("1", "ADMIN").subscribe().withSubscriber(UniAssertSubscriber.create());

    List<SeccionesDTOOptimized> result = subscriber.awaitItem().getItem();
    assertNotNull(result);
  }

  // Helper methods to create mock data
  private Object[] createMockCompleteRow() {
    return new Object[] {1L, "Test Seccion", 1, "S", null, "N", // seccion data
        100L, 1, "S", // campo_seccion data
        50L, "Test Campo", "TEXT", "Default Value", "S", "20px", "Example", "Help", "S", "MASK",
        "validation", "S", 1, 10, // campo data
        200L, null, "Test Catalogo", "S", // catalogo data
        300L, "Test Valor", "S", // valor_catalogo data
        400L, "E", "ADMIN", // role permission data
        10L, "Test Etapa", "S", // etapa data
        500L, "S" // rol and rpcs_activo data
    };
  }

  private Object[] createMockCompleteRowWithDetalle() {
    Object[] baseRow = createMockCompleteRow();
    Object[] rowWithDetalle = Arrays.copyOf(baseRow, baseRow.length + 3);

    // Add detalle solicitud data at the end
    rowWithDetalle[baseRow.length] = 999L; // id_detalle_solicitud
    rowWithDetalle[baseRow.length + 1] = 100L; // campo_seccion
    rowWithDetalle[baseRow.length + 2] = "Detalle Test Value"; // detalle_valor

    return rowWithDetalle;
  }

  private Object[] createMockParentSectionRow() {
    return new Object[] {1L, "Parent Section", 1, "S", null, "N", // parent seccion
        null, null, null, // no campo_seccion
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, // no
                                                                                            // campo
                                                                                            // data
        null, null, null, null, // no catalogo data
        null, null, null, // no valor_catalogo data
        null, null, null, // no role permission data
        10L, "Test Etapa", "S", // etapa data
        null, null // no rol data
    };
  }

  private Object[] createMockChildSectionRow() {
    return new Object[] {2L, "Child Section", 2, "S", 1L, "N", // child seccion with idSuperior = 1L
        null, null, null, // no campo_seccion
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, // no
                                                                                            // campo
                                                                                            // data
        null, null, null, null, // no catalogo data
        null, null, null, // no valor_catalogo data
        null, null, null, // no role permission data
        10L, "Test Etapa", "S", // etapa data
        null, null // no rol data
    };
  }

  private Object[] createMockRowWithParentCatalog() {
    return new Object[] {1L, "Test Seccion", 1, "S", null, "N", // seccion data
        100L, 1, "S", // campo_seccion data
        50L, "Test Campo", "TEXT", "Default Value", "S", "20px", "Example", "Help", "S", "MASK",
        "validation", "S", 1, 10, // campo data
        200L, null, "Parent Catalogo", "S", // parent catalogo data
        null, null, null, // no valor_catalogo data
        null, null, null, // no role permission data
        10L, "Test Etapa", "S", // etapa data
        null, null // no rol data
    };
  }

  private Object[] createMockRowWithChildCatalog() {
    return new Object[] {1L, "Test Seccion", 1, "S", null, "N", // seccion data
        101L, 2, "S", // different campo_seccion data
        51L, "Test Campo 2", "TEXT", "Default Value", "S", "20px", "Example", "Help", "S", "MASK",
        "validation", "S", 1, 10, // campo data
        201L, 200L, "Child Catalogo", "S", // child catalogo data with idSuperior = 200L
        null, null, null, // no valor_catalogo data
        null, null, null, // no role permission data
        10L, "Test Etapa", "S", // etapa data
        null, null // no rol data
    };
  }
}
