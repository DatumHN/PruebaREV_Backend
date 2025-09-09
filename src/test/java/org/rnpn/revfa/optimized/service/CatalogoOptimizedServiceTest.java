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
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.rnpn.revfa.optimized.dto.CatalogoDTOOptimized;
import org.rnpn.revfa.optimized.dto.ValorCatalogoDTOOptimized;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.function.Function;

@QuarkusTest
class CatalogoOptimizedServiceTest {

  @Inject
  CatalogoOptimizedService catalogoOptimizedService;

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
  @DisplayName("Should return catalogo with valores when data exists")
  void testGetCatalogoHijo_WithValidData() {
    Long idSuperior = 1L;
    Long catalogoId = 2L;

    Object[] mockRow = {2L, // ID_CATALOGO
        1L, // ID_SUPERIOR
        "Test Catalogo", // NOMBRE
        "S", // ACTIVO
        10L, // ID_VALOR_CATALOGO
        "Test Valor", // VALOR
        "S" // VC_ACTIVO
    };

    List<Object> mockResults = Arrays.asList(mockRow);

    when(sessionFactory.withSession(any())).thenAnswer(invocation -> {
      Function<Mutiny.Session, Uni<?>> function = invocation.getArgument(0);
      return function.apply(mockSession);
    });

    when(mockSession.createNativeQuery(anyString())).thenReturn(mockQuery);
    when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
    when(mockQuery.getResultList()).thenReturn(Uni.createFrom().item(mockResults));

    UniAssertSubscriber<CatalogoDTOOptimized> subscriber =
        catalogoOptimizedService.getCatalogoHijo(idSuperior, catalogoId).subscribe()
            .withSubscriber(UniAssertSubscriber.create());

    CatalogoDTOOptimized result = subscriber.awaitItem().getItem();

    assertNotNull(result);
    assertEquals(2L, result.id);
    assertEquals(1L, result.idSuperior);
    assertEquals("Test Catalogo", result.nombre);
    assertEquals('S', result.activo);
    assertFalse(result.subCatalogo);
    assertNotNull(result.valoresCatalogos);
    assertEquals(1, result.valoresCatalogos.size());

    ValorCatalogoDTOOptimized valor = result.valoresCatalogos.get(0);
    assertEquals(10L, valor.id);
    assertEquals("Test Valor", valor.valor);
    assertEquals('S', valor.activo);
  }

  @Test
  @DisplayName("Should return null when no data exists")
  void testGetCatalogoHijo_WithEmptyResults() {
    Long idSuperior = 1L;
    Long catalogoId = 999L;

    List<Object> emptyResults = Arrays.asList();

    when(sessionFactory.withSession(any())).thenAnswer(invocation -> {
      Function<Mutiny.Session, Uni<?>> function = invocation.getArgument(0);
      return function.apply(mockSession);
    });

    when(mockSession.createNativeQuery(anyString())).thenReturn(mockQuery);
    when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
    when(mockQuery.getResultList()).thenReturn(Uni.createFrom().item(emptyResults));

    UniAssertSubscriber<CatalogoDTOOptimized> subscriber =
        catalogoOptimizedService.getCatalogoHijo(idSuperior, catalogoId).subscribe()
            .withSubscriber(UniAssertSubscriber.create());

    CatalogoDTOOptimized result = subscriber.awaitItem().getItem();
    assertNull(result);
  }

  @Test
  @DisplayName("Should return catalogo without valores when only catalogo data exists")
  void testGetCatalogoHijo_WithoutValores() {
    Long idSuperior = 1L;
    Long catalogoId = 2L;

    Object[] mockRow = {2L, // ID_CATALOGO
        1L, // ID_SUPERIOR
        "Test Catalogo", // NOMBRE
        "S", // ACTIVO
        null, // ID_VALOR_CATALOGO
        null, // VALOR
        null // VC_ACTIVO
    };

    List<Object> mockResults = Arrays.asList(mockRow);

    when(sessionFactory.withSession(any())).thenAnswer(invocation -> {
      Function<Mutiny.Session, Uni<?>> function = invocation.getArgument(0);
      return function.apply(mockSession);
    });

    when(mockSession.createNativeQuery(anyString())).thenReturn(mockQuery);
    when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
    when(mockQuery.getResultList()).thenReturn(Uni.createFrom().item(mockResults));

    UniAssertSubscriber<CatalogoDTOOptimized> subscriber =
        catalogoOptimizedService.getCatalogoHijo(idSuperior, catalogoId).subscribe()
            .withSubscriber(UniAssertSubscriber.create());

    CatalogoDTOOptimized result = subscriber.awaitItem().getItem();

    assertNotNull(result);
    assertEquals(2L, result.id);
    assertEquals("Test Catalogo", result.nombre);
    assertNotNull(result.valoresCatalogos);
    assertTrue(result.valoresCatalogos.isEmpty());
  }

  @Test
  @DisplayName("Should return true when subcatalogos exist")
  void testHasSubCatalogos_ReturnsTrue() {
    Long catalogoId = 1L;
    Long count = 3L;

    when(sessionFactory.withSession(any())).thenAnswer(invocation -> {
      Function<Mutiny.Session, Uni<?>> function = invocation.getArgument(0);
      return function.apply(mockSession);
    });

    when(mockSession.createNativeQuery(anyString())).thenReturn(mockQuery);
    when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
    when(mockQuery.getSingleResult()).thenReturn(Uni.createFrom().item(count));

    UniAssertSubscriber<Boolean> subscriber = catalogoOptimizedService.hasSubCatalogos(catalogoId)
        .subscribe().withSubscriber(UniAssertSubscriber.create());

    Boolean result = subscriber.awaitItem().getItem();
    assertTrue(result);
  }

  @Test
  @DisplayName("Should return false when no subcatalogos exist")
  void testHasSubCatalogos_ReturnsFalse() {
    Long catalogoId = 1L;
    Long count = 0L;

    when(sessionFactory.withSession(any())).thenAnswer(invocation -> {
      Function<Mutiny.Session, Uni<?>> function = invocation.getArgument(0);
      return function.apply(mockSession);
    });

    when(mockSession.createNativeQuery(anyString())).thenReturn(mockQuery);
    when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
    when(mockQuery.getSingleResult()).thenReturn(Uni.createFrom().item(count));

    UniAssertSubscriber<Boolean> subscriber = catalogoOptimizedService.hasSubCatalogos(catalogoId)
        .subscribe().withSubscriber(UniAssertSubscriber.create());

    Boolean result = subscriber.awaitItem().getItem();
    assertFalse(result);
  }

  @Test
  @DisplayName("Should avoid duplicate valores in result")
  void testGetCatalogoHijo_AvoidsDuplicateValores() {
    Long idSuperior = 1L;
    Long catalogoId = 2L;

    Object[] mockRow1 = {2L, "Test Catalogo", 1, "S", 1L, 10L, "Test Valor", "S"};
    Object[] mockRow2 = {2L, "Test Catalogo", 1, "S", 1L, 10L, "Test Valor", "S" // Duplicate
    };
    Object[] mockRow3 = {2L, "Test Catalogo", 1, "S", 1L, 11L, "Another Valor", "S"};

    List<Object> mockResults = Arrays.asList(mockRow1, mockRow2, mockRow3);

    when(sessionFactory.withSession(any())).thenAnswer(invocation -> {
      Function<Mutiny.Session, Uni<?>> function = invocation.getArgument(0);
      return function.apply(mockSession);
    });

    when(mockSession.createNativeQuery(anyString())).thenReturn(mockQuery);
    when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
    when(mockQuery.getResultList()).thenReturn(Uni.createFrom().item(mockResults));

    UniAssertSubscriber<CatalogoDTOOptimized> subscriber =
        catalogoOptimizedService.getCatalogoHijo(idSuperior, catalogoId).subscribe()
            .withSubscriber(UniAssertSubscriber.create());

    CatalogoDTOOptimized result = subscriber.awaitItem().getItem();

    assertNotNull(result);
    assertEquals(2, result.valoresCatalogos.size()); // Should be 2, not 3
  }
}
