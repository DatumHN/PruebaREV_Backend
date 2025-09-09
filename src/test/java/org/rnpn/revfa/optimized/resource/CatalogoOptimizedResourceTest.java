package org.rnpn.revfa.optimized.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.restassured.RestAssured;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rnpn.revfa.optimized.dto.CatalogoDTOOptimized;
import org.rnpn.revfa.optimized.dto.ValorCatalogoDTOOptimized;
import org.rnpn.revfa.optimized.service.CatalogoOptimizedService;

import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@QuarkusTest
class CatalogoOptimizedResourceTest {

  @InjectMock
  CatalogoOptimizedService catalogoOptimizedService;

  @Test
  @DisplayName("Should return catalogo with subcatalogo flag when data exists")
  void testGetCatalogoHijo_Success() {
    Long idSuperior = 1L;
    Long catalogoId = 2L;

    // Mock catalogo data
    CatalogoDTOOptimized catalogo = new CatalogoDTOOptimized();
    catalogo.id = catalogoId;
    catalogo.idSuperior = idSuperior;
    catalogo.nombre = "Test Catalogo";
    catalogo.activo = 'S';
    catalogo.subCatalogo = false;

    ValorCatalogoDTOOptimized valor = new ValorCatalogoDTOOptimized();
    valor.id = 10L;
    valor.valor = "Test Valor";
    valor.activo = 'S';

    catalogo.valoresCatalogos = Arrays.asList(valor);

    when(catalogoOptimizedService.getCatalogoHijo(idSuperior, catalogoId))
        .thenReturn(Uni.createFrom().item(catalogo));
    when(catalogoOptimizedService.hasSubCatalogos(catalogoId))
        .thenReturn(Uni.createFrom().item(true));

    given().queryParam("idSuperior", idSuperior).queryParam("catalogoId", catalogoId).when()
        .get("/catalogo-optimized/catalogoHijo").then().statusCode(200)
        .body("id", equalTo(catalogoId.intValue()))
        .body("idSuperior", equalTo(idSuperior.intValue())).body("nombre", equalTo("Test Catalogo"))
        .body("activo", equalTo("S")).body("subCatalogo", equalTo(true))
        .body("valoresCatalogos", hasSize(1)).body("valoresCatalogos[0].id", equalTo(10))
        .body("valoresCatalogos[0].valor", equalTo("Test Valor"))
        .body("valoresCatalogos[0].activo", equalTo("S"));
  }

  @Test
  @DisplayName("Should return catalogo with subCatalogo false when no subcatalogos exist")
  void testGetCatalogoHijo_NoSubCatalogos() {
    Long idSuperior = 1L;
    Long catalogoId = 2L;

    CatalogoDTOOptimized catalogo = new CatalogoDTOOptimized();
    catalogo.id = catalogoId;
    catalogo.idSuperior = idSuperior;
    catalogo.nombre = "Test Catalogo";
    catalogo.activo = 'S';
    catalogo.subCatalogo = false;
    catalogo.valoresCatalogos = Arrays.asList();

    when(catalogoOptimizedService.getCatalogoHijo(idSuperior, catalogoId))
        .thenReturn(Uni.createFrom().item(catalogo));
    when(catalogoOptimizedService.hasSubCatalogos(catalogoId))
        .thenReturn(Uni.createFrom().item(false));

    given().queryParam("idSuperior", idSuperior).queryParam("catalogoId", catalogoId).when()
        .get("/catalogo-optimized/catalogoHijo").then().statusCode(200)
        .body("id", equalTo(catalogoId.intValue())).body("subCatalogo", equalTo(false))
        .body("valoresCatalogos", hasSize(0));
  }

  @Test
  @DisplayName("Should return null when catalogo not found")
  void testGetCatalogoHijo_NotFound() {
    Long idSuperior = 1L;
    Long catalogoId = 999L;

    when(catalogoOptimizedService.getCatalogoHijo(idSuperior, catalogoId))
        .thenReturn(Uni.createFrom().nullItem());

    given().queryParam("idSuperior", idSuperior).queryParam("catalogoId", catalogoId).when()
        .get("/catalogo-optimized/catalogoHijo").then().statusCode(204); // No content when null is
                                                                         // returned
  }

  @Test
  @DisplayName("Should handle missing query parameters")
  void testGetCatalogoHijo_MissingParameters() {
    given().queryParam("idSuperior", 1L)
        // Missing catalogoId parameter
        .when().get("/catalogo-optimized/catalogoHijo").then().statusCode(400); // Bad request due
                                                                                // to missing
                                                                                // parameter
  }

  @Test
  @DisplayName("Should handle service errors gracefully")
  void testGetCatalogoHijo_ServiceError() {
    Long idSuperior = 1L;
    Long catalogoId = 2L;

    CatalogoDTOOptimized catalogo = new CatalogoDTOOptimized();
    catalogo.id = catalogoId;

    when(catalogoOptimizedService.getCatalogoHijo(idSuperior, catalogoId))
        .thenReturn(Uni.createFrom().item(catalogo));
    when(catalogoOptimizedService.hasSubCatalogos(catalogoId))
        .thenReturn(Uni.createFrom().failure(new RuntimeException("Database error")));

    given().queryParam("idSuperior", idSuperior).queryParam("catalogoId", catalogoId).when()
        .get("/catalogo-optimized/catalogoHijo").then().statusCode(500); // Internal server error
  }

  @Test
  @DisplayName("Should accept valid Long parameter values")
  void testGetCatalogoHijo_ValidParameters() {
    Long idSuperior = Long.MAX_VALUE - 1;
    Long catalogoId = Long.MAX_VALUE - 2;

    CatalogoDTOOptimized catalogo = new CatalogoDTOOptimized();
    catalogo.id = catalogoId;
    catalogo.idSuperior = idSuperior;
    catalogo.nombre = "Test";
    catalogo.activo = 'S';
    catalogo.valoresCatalogos = Arrays.asList();

    when(catalogoOptimizedService.getCatalogoHijo(idSuperior, catalogoId))
        .thenReturn(Uni.createFrom().item(catalogo));
    when(catalogoOptimizedService.hasSubCatalogos(catalogoId))
        .thenReturn(Uni.createFrom().item(false));

    given().queryParam("idSuperior", idSuperior).queryParam("catalogoId", catalogoId).when()
        .get("/catalogo-optimized/catalogoHijo").then().statusCode(200);
  }
}
