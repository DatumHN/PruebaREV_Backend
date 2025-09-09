package org.rnpn.revfa.optimized.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rnpn.revfa.entity.TipoDocumento;
import org.rnpn.revfa.entity.Campo;
import org.rnpn.revfa.optimized.dto.DetalleSolicitudDTOOptimized;
import org.rnpn.revfa.optimized.dto.SolicitudDTOOptimized;
import org.rnpn.revfa.optimized.service.SolicitudesOptimizedService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@QuarkusTest
class SolicitudesOptimizedResourceTest {

  @InjectMock
  SolicitudesOptimizedService solicitudesOptimizedService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @DisplayName("Should return list of solicitudes")
  void testGetSolicitudes_Success() {
    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.id = 10L;
    tipoDocumento.nombre = "Tipo Test";
    tipoDocumento.activo = 'S';
    tipoDocumento.idSuperior = 5L;

    SolicitudDTOOptimized solicitud = new SolicitudDTOOptimized();
    solicitud.id = 1L;
    solicitud.correlativo = "CORR-001";
    solicitud.estado = "PENDIENTE";
    solicitud.uuid = "uuid-123";
    solicitud.fechaSolicitud = Date.valueOf(LocalDate.of(2024, 1, 15));
    solicitud.tipoDocumento = tipoDocumento;

    List<SolicitudDTOOptimized> solicitudes = Arrays.asList(solicitud);

    when(solicitudesOptimizedService.getSolicitudes())
        .thenReturn(Uni.createFrom().item(solicitudes));

    given().when().get("/solicitudes-optimized").then().statusCode(200).body("", hasSize(1))
        .body("[0].id", equalTo(1)).body("[0].correlativo", equalTo("CORR-001"))
        .body("[0].estado", equalTo("PENDIENTE")).body("[0].uuid", equalTo("uuid-123"))
        .body("[0].tipoDocumento.id", equalTo(10))
        .body("[0].tipoDocumento.nombre", equalTo("Tipo Test"))
        .body("[0].tipoDocumento.activo", equalTo("S"));
  }

  @Test
  @DisplayName("Should return empty list when no solicitudes exist")
  void testGetSolicitudes_Empty() {
    when(solicitudesOptimizedService.getSolicitudes())
        .thenReturn(Uni.createFrom().item(Collections.emptyList()));

    given()
        .when()
        .get("/solicitudes-optimized")
        .then()
        .statusCode(200)
        .body("", hasSize(0));
  }

  @Test
  @DisplayName("Should update estado successfully")
  void testActualizarEstado_Success() {
    String estado = "APROBADO";
    Long idSolicitud = 1L;

    when(solicitudesOptimizedService.actualizarEstado(estado, idSolicitud))
        .thenReturn(Uni.createFrom().voidItem());

    given().when()
        .put("/solicitudes-optimized/estado/{estado}/solicitud/{idSolicitud}", estado, idSolicitud)
        .then().statusCode(200).body("message", equalTo("Estado actualizado con éxito"));
  }

  @Test
  @DisplayName("Should return 404 when solicitud not found for estado update")
  void testActualizarEstado_NotFound() {
    String estado = "APROBADO";
    Long idSolicitud = 999L;

    when(solicitudesOptimizedService.actualizarEstado(estado, idSolicitud))
        .thenReturn(Uni.createFrom().failure(new NotFoundException("Solicitud no encontrada")));

    given().when()
        .put("/solicitudes-optimized/estado/{estado}/solicitud/{idSolicitud}", estado, idSolicitud)
        .then().statusCode(404).body("error", equalTo("Solicitud no encontrada"));
  }

  @Test
  @DisplayName("Should return 500 on service error for estado update")
  void testActualizarEstado_ServiceError() {
    String estado = "APROBADO";
    Long idSolicitud = 1L;

    when(solicitudesOptimizedService.actualizarEstado(estado, idSolicitud))
        .thenReturn(Uni.createFrom().failure(new RuntimeException("Database error")));

    given().when()
        .put("/solicitudes-optimized/estado/{estado}/solicitud/{idSolicitud}", estado, idSolicitud)
        .then().statusCode(500).body("error", equalTo("Database error"));
  }

  @Test
  @DisplayName("Should update solicitud successfully")
  void testActualizarSolicitud_Success() throws Exception {
    Long id = 1L;
    SolicitudDTOOptimized solicitud = new SolicitudDTOOptimized();
    solicitud.estado = "APROBADO";

    when(solicitudesOptimizedService.actualizarSolicitud(eq(id), any(SolicitudDTOOptimized.class)))
        .thenReturn(Uni.createFrom().item("Actualización exitosa"));

    String requestBody = objectMapper.writeValueAsString(solicitud);

    given().contentType(ContentType.JSON).body(requestBody).when()
        .put("/solicitudes-optimized/actualizar/{id}", id).then().statusCode(200)
        .body("message", equalTo("Actualización exitosa"));
  }

  @Test
  @DisplayName("Should update solicitud with detalles successfully")
  void testActualizarSolicitud_WithDetalles() throws Exception {
    Long id = 1L;

    Campo campo = new Campo();
    campo.id = 5L;

    DetalleSolicitudDTOOptimized detalle = new DetalleSolicitudDTOOptimized();
    detalle.id = 100L;
    detalle.campos = campo;
    detalle.valor = "Nuevo valor";
    detalle.campoSeccion = 10L;

    SolicitudDTOOptimized solicitud = new SolicitudDTOOptimized();
    solicitud.estado = "APROBADO";
    solicitud.detallesSolicitudes = Arrays.asList(detalle);

    when(solicitudesOptimizedService.actualizarSolicitud(eq(id), any(SolicitudDTOOptimized.class)))
        .thenReturn(Uni.createFrom().item("Detalles actualizados correctamente"));

    String requestBody = objectMapper.writeValueAsString(solicitud);

    given().contentType(ContentType.JSON).body(requestBody).when()
        .put("/solicitudes-optimized/actualizar/{id}", id).then().statusCode(200)
        .body("message", equalTo("Detalles actualizados correctamente"));
  }

  @Test
  @DisplayName("Should return 404 when solicitud not found for update")
  void testActualizarSolicitud_NotFound() throws Exception {
    Long id = 999L;
    SolicitudDTOOptimized solicitud = new SolicitudDTOOptimized();
    solicitud.estado = "APROBADO";

    when(solicitudesOptimizedService.actualizarSolicitud(eq(id), any(SolicitudDTOOptimized.class)))
        .thenReturn(Uni.createFrom().failure(new NotFoundException("Solicitud no encontrada")));

    String requestBody = objectMapper.writeValueAsString(solicitud);

    given().contentType(ContentType.JSON).body(requestBody).when()
        .put("/solicitudes-optimized/actualizar/{id}", id).then().statusCode(404)
        .body("error", equalTo("Solicitud no encontrada"));
  }

  @Test
  @DisplayName("Should return 500 on service error for solicitud update")
  void testActualizarSolicitud_ServiceError() throws Exception {
    Long id = 1L;
    SolicitudDTOOptimized solicitud = new SolicitudDTOOptimized();
    solicitud.estado = "APROBADO";

    when(solicitudesOptimizedService.actualizarSolicitud(eq(id), any(SolicitudDTOOptimized.class)))
        .thenReturn(Uni.createFrom().failure(new RuntimeException("Database error")));

    String requestBody = objectMapper.writeValueAsString(solicitud);

    given().contentType(ContentType.JSON).body(requestBody).when()
        .put("/solicitudes-optimized/actualizar/{id}", id).then().statusCode(500)
        .body("error", equalTo(
            "Ocurrió un error al actualizar la solicitud, contáctese con el administrador"));
  }

  @Test
  @DisplayName("Should handle invalid JSON in request body")
  void testActualizarSolicitud_InvalidJson() {
    Long id = 1L;
    String invalidJson = "{invalid-json}";

    given().contentType(ContentType.JSON).body(invalidJson).when()
        .put("/solicitudes-optimized/actualizar/{id}", id).then().statusCode(400); // Bad request
                                                                                   // due to invalid
                                                                                   // JSON
  }

  @Test
  @DisplayName("Should handle missing request body")
  void testActualizarSolicitud_MissingBody() {
    Long id = 1L;

    given().contentType(ContentType.JSON).when().put("/solicitudes-optimized/actualizar/{id}", id)
        .then().statusCode(400); // Bad request due to missing body
  }

  @Test
  @DisplayName("Should handle invalid path parameter")
  void testActualizarSolicitud_InvalidPathParam() throws Exception {
    SolicitudDTOOptimized solicitud = new SolicitudDTOOptimized();
    solicitud.estado = "APROBADO";
    String requestBody = objectMapper.writeValueAsString(solicitud);

    given().contentType(ContentType.JSON).body(requestBody).when()
        .put("/solicitudes-optimized/actualizar/{id}", "invalid-id").then().statusCode(400); // Bad
                                                                                             // request
                                                                                             // due
                                                                                             // to
                                                                                             // invalid
                                                                                             // path
                                                                                             // parameter
  }

  @Test
  @DisplayName("Should handle service timeout gracefully")
  void testGetSolicitudes_ServiceTimeout() {
    when(solicitudesOptimizedService.getSolicitudes())
        .thenReturn(Uni.createFrom().failure(new RuntimeException("Timeout")));

    given()
        .when()
        .get("/solicitudes-optimized")
        .then()
        .statusCode(500);
  }
}
