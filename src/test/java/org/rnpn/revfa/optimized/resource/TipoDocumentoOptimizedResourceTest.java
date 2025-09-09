package org.rnpn.revfa.optimized.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rnpn.revfa.optimized.dto.*;
import org.rnpn.revfa.optimized.service.TipoDocumentoOptimizedService;
import org.rnpn.revfa.optimized.util.PerformanceComparator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@QuarkusTest
class TipoDocumentoOptimizedResourceTest {

  @InjectMock
  TipoDocumentoOptimizedService tipoDocumentoOptimizedService;

  @InjectMock
  PerformanceComparator performanceComparator;

  @Test
  @DisplayName("Should return sections with entity graph")
  void testSeccionesWithEntityGraph_Success() {
    String id = "1";
    String rol = "ADMIN";

    List<SeccionesDTOOptimized> secciones = createMockSecciones();

    when(tipoDocumentoOptimizedService.seccionesOptimized(id, rol))
        .thenReturn(Uni.createFrom().item(secciones));

    given().queryParam("rol", rol).when()
        .get("/optimized/tipodocumentos/{id}/secciones/entity-graph", id).then().statusCode(200)
        .body("", hasSize(1)).body("[0].id", equalTo(1)).body("[0].nombre", equalTo("Test Seccion"))
        .body("[0].secuencia", equalTo(1)).body("[0].activo", equalTo("S"))
        .body("[0].etapa.id", equalTo(10)).body("[0].etapa.nombre", equalTo("Test Etapa"))
        .body("[0].camposSecciones", hasSize(1)).body("[0].camposSecciones[0].id", equalTo(100))
        .body("[0].camposSecciones[0].campo.id", equalTo(50))
        .body("[0].camposSecciones[0].campo.nombre", equalTo("Test Campo"));
  }

  @Test
  @DisplayName("Should return sections complete with entity graph")
  void testSeccionesCompWithEntityGraph_Success() {
    String id = "1";
    String rol = "ADMIN";
    String idSolicitud = "123";

    List<SeccionesDTOOptimized> secciones = createMockSeccionesWithDetalles();

    when(tipoDocumentoOptimizedService.seccionesCompleteOptimized(id, rol, idSolicitud))
        .thenReturn(Uni.createFrom().item(secciones));

    given().queryParam("rol", rol).queryParam("idSolicitud", idSolicitud).when()
        .get("/optimized/tipodocumentos/{id}/seccionesCompl/entity-graph", id).then()
        .statusCode(200).body("", hasSize(1))
        .body("[0].camposSecciones[0].campo.detallesSolicitudes", hasSize(1))
        .body("[0].camposSecciones[0].campo.detallesSolicitudes[0].id", equalTo(999))
        .body("[0].camposSecciones[0].campo.detallesSolicitudes[0].valor", equalTo("Test Value"));
  }

  @Test
  @DisplayName("Should return sections with batch size")
  void testSeccionesWithBatchSize_Success() {
    String id = "1";
    String rol = "ADMIN";

    List<SeccionesDTOOptimized> secciones = createMockSecciones();

    when(tipoDocumentoOptimizedService.seccionesBatched(id, rol))
        .thenReturn(Uni.createFrom().item(secciones));

    given().queryParam("rol", rol).when()
        .get("/optimized/tipodocumentos/{id}/secciones/batch-size", id).then().statusCode(200)
        .body("", hasSize(1));
  }

  @Test
  @DisplayName("Should return performance comparison data")
  void testComparePerformance_Success() {
    String id = "1";
    String rol = "ADMIN";

    Map<String, Object> performanceData = new HashMap<>();
    performanceData.put("entityGraph", Map.of("executionTime", 150, "queryCount", 1));
    performanceData.put("batchSize", Map.of("executionTime", 200, "queryCount", 3));
    performanceData.put("recommendation", "Use Entity Graph for better performance");

    when(performanceComparator.comparePerformance(id, rol))
        .thenReturn(Uni.createFrom().item(performanceData));

    given().queryParam("rol", rol).when()
        .get("/optimized/tipodocumentos/{id}/performance-comparison", id).then().statusCode(200)
        .body("entityGraph.executionTime", equalTo(150))
        .body("batchSize.executionTime", equalTo(200))
        .body("recommendation", equalTo("Use Entity Graph for better performance"));
  }

  @Test
  @DisplayName("Should handle missing rol parameter")
  void testSeccionesWithEntityGraph_MissingRol() {
    String id = "1";

    given()
        // Missing rol parameter
        .when().get("/optimized/tipodocumentos/{id}/secciones/entity-graph", id).then()
        .statusCode(400); // Bad request due to missing required parameter
  }

  @Test
  @DisplayName("Should handle service errors gracefully")
  void testSeccionesWithEntityGraph_ServiceError() {
    String id = "1";
    String rol = "ADMIN";

    when(tipoDocumentoOptimizedService.seccionesOptimized(id, rol))
        .thenReturn(Uni.createFrom().failure(new RuntimeException("Database connection failed")));

    given().queryParam("rol", rol).when()
        .get("/optimized/tipodocumentos/{id}/secciones/entity-graph", id).then().statusCode(500);
  }

  @Test
  @DisplayName("Should handle empty results")
  void testSeccionesWithEntityGraph_EmptyResults() {
    String id = "999";
    String rol = "ADMIN";

    when(tipoDocumentoOptimizedService.seccionesOptimized(id, rol))
        .thenReturn(Uni.createFrom().item(Collections.emptyList()));

    given().queryParam("rol", rol).when()
        .get("/optimized/tipodocumentos/{id}/secciones/entity-graph", id).then().statusCode(200)
        .body("", hasSize(0));
  }

  @Test
  @DisplayName("Should handle invalid tipo documento ID")
  void testSeccionesWithEntityGraph_InvalidId() {
    String invalidId = "invalid";
    String rol = "ADMIN";

    // Service should handle the invalid ID gracefully
    when(tipoDocumentoOptimizedService.seccionesOptimized(invalidId, rol))
        .thenReturn(Uni.createFrom().item(Collections.emptyList()));

    given().queryParam("rol", rol).when()
        .get("/optimized/tipodocumentos/{id}/secciones/entity-graph", invalidId).then()
        .statusCode(200).body("", hasSize(0));
  }

  @Test
  @DisplayName("Should handle sections complete with missing idSolicitud parameter")
  void testSeccionesCompWithEntityGraph_MissingIdSolicitud() {
    String id = "1";
    String rol = "ADMIN";

    given().queryParam("rol", rol)
        // Missing idSolicitud parameter
        .when().get("/optimized/tipodocumentos/{id}/seccionesCompl/entity-graph", id).then()
        .statusCode(400); // Bad request due to missing required parameter
  }

  @Test
  @DisplayName("Should handle performance comparison service error")
  void testComparePerformance_ServiceError() {
    String id = "1";
    String rol = "ADMIN";

    when(performanceComparator.comparePerformance(id, rol))
        .thenReturn(Uni.createFrom().failure(new RuntimeException("Performance analysis failed")));

    given().queryParam("rol", rol).when()
        .get("/optimized/tipodocumentos/{id}/performance-comparison", id).then().statusCode(500);
  }

  @Test
  @DisplayName("Should handle long values for IDs")
  void testSeccionesWithEntityGraph_LongValues() {
    String id = String.valueOf(Long.MAX_VALUE);
    String rol = "ADMIN";

    when(tipoDocumentoOptimizedService.seccionesOptimized(id, rol))
        .thenReturn(Uni.createFrom().item(Collections.emptyList()));

    given().queryParam("rol", rol).when()
        .get("/optimized/tipodocumentos/{id}/secciones/entity-graph", id).then().statusCode(200);
  }

  @Test
  @DisplayName("Should handle special characters in rol parameter")
  void testSeccionesWithEntityGraph_SpecialCharsInRol() {
    String id = "1";
    String rol = "ADMIN-TEST_USER";

    when(tipoDocumentoOptimizedService.seccionesOptimized(id, rol))
        .thenReturn(Uni.createFrom().item(Collections.emptyList()));

    given().queryParam("rol", rol).when()
        .get("/optimized/tipodocumentos/{id}/secciones/entity-graph", id).then().statusCode(200);
  }

  // Helper methods to create mock data
  private List<SeccionesDTOOptimized> createMockSecciones() {
    SeccionesDTOOptimized seccion = new SeccionesDTOOptimized();
    seccion.id = 1L;
    seccion.nombre = "Test Seccion";
    seccion.secuencia = 1;
    seccion.activo = 'S';
    seccion.ventanaEmergente = 'N';

    EtapaDTOOptimized etapa = new EtapaDTOOptimized();
    etapa.id = 10L;
    etapa.nombre = "Test Etapa";
    etapa.activo = 'S';
    seccion.etapa = etapa;

    CampoSeccionDTOOptimized campoSeccion = new CampoSeccionDTOOptimized();
    campoSeccion.id = 100L;
    campoSeccion.secuencia = 1;
    campoSeccion.activo = 'S';

    CampoDTOOptimized campo = new CampoDTOOptimized();
    campo.id = 50L;
    campo.nombre = "Test Campo";
    campo.tipo = "TEXT";
    campo.obligatorio = 'S';
    campo.detallesSolicitudes = Collections.emptyList();

    CatalogoDTOOptimized catalogo = new CatalogoDTOOptimized();
    catalogo.id = 200L;
    catalogo.nombre = "Test Catalogo";
    catalogo.activo = 'S';
    catalogo.subCatalogo = false;
    catalogo.valoresCatalogos = Collections.emptyList();

    campo.catalogos = catalogo;
    campoSeccion.campo = campo;
    campoSeccion.rolesPermisos = Collections.emptyList();

    seccion.camposSecciones = Arrays.asList(campoSeccion);
    seccion.subSecciones = Collections.emptyList();

    return Arrays.asList(seccion);
  }

  private List<SeccionesDTOOptimized> createMockSeccionesWithDetalles() {
    List<SeccionesDTOOptimized> secciones = createMockSecciones();

    DetalleSolicitudDTOOptimized detalle = new DetalleSolicitudDTOOptimized();
    detalle.id = 999L;
    detalle.campoSeccion = 100L;
    detalle.valor = "Test Value";

    secciones.get(0).camposSecciones.get(0).campo.detallesSolicitudes = Arrays.asList(detalle);

    return secciones;
  }
}
