package org.rnpn.revfa.optimized.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;
import org.rnpn.revfa.optimized.dto.CatalogoDTOOptimized;
import org.rnpn.revfa.optimized.dto.ValorCatalogoDTOOptimized;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CatalogoOptimizedService {

  @Inject
  Mutiny.SessionFactory sessionFactory;

  public Uni<CatalogoDTOOptimized> getCatalogoHijo(Long idSuperior, Long catalogoId) {
    String query = """
        SELECT
            c.ID_CATALOGO,
            c.ID_SUPERIOR,
            c.NOMBRE,
            c.ACTIVO,
            vc.ID_VALOR_CATALOGO,
            vc.VALOR,
            vc.ACTIVO as VC_ACTIVO
        FROM CATALOGOS c
        LEFT JOIN VALORES_CATALOGOS vc ON c.ID_CATALOGO = vc.ID_CATALOGO
        WHERE vc.ID_SUPERIOR = :idSuperior
        AND c.ID_CATALOGO = :catalogoId
        ORDER BY c.ID_CATALOGO, vc.ID_VALOR_CATALOGO
        """;

    return sessionFactory.withSession(session -> session.createNativeQuery(query)
        .setParameter("idSuperior", idSuperior).setParameter("catalogoId", catalogoId)
        .getResultList().map(results -> this.buildCatalogoWithValues(results)));
  }

  public Uni<Boolean> hasSubCatalogos(Long id) {
    String countQuery = "SELECT COUNT(*) FROM CATALOGOS WHERE ID_SUPERIOR = :id";

    return sessionFactory.withSession(session -> session.createNativeQuery(countQuery)
        .setParameter("id", id).getSingleResult().map(count -> ((Number) count).longValue() > 0));
  }

  private CatalogoDTOOptimized buildCatalogoWithValues(List<?> results) {
    if (results.isEmpty()) {
      return null;
    }

    CatalogoDTOOptimized catalogo = null;
    List<ValorCatalogoDTOOptimized> valores = new ArrayList<>();

    for (Object result : results) {
      Object[] row = (Object[]) result;

      // Build catalogo only once (first row)
      if (catalogo == null) {
        catalogo = new CatalogoDTOOptimized();
        catalogo.id = row[0] != null ? ((Number) row[0]).longValue() : null;
        catalogo.idSuperior = row[1] != null ? ((Number) row[1]).longValue() : null;
        catalogo.nombre = (String) row[2];
        catalogo.activo = row[3] != null ? ((String) row[3]).charAt(0) : null;
        catalogo.subCatalogo = false;
        catalogo.valoresCatalogos = new ArrayList<>();
      }

      // Add valor_catalogo if exists
      if (row[4] != null) {
        Long valorId = ((Number) row[4]).longValue();

        // Avoid duplicates
        boolean exists = valores.stream().anyMatch(v -> v.id.equals(valorId));
        if (!exists) {
          ValorCatalogoDTOOptimized valor = new ValorCatalogoDTOOptimized();
          valor.id = valorId;
          valor.valor = (String) row[5];
          valor.descripcion = null;
          valor.activo = row[6] != null ? ((String) row[6]).charAt(0) : null;
          valores.add(valor);
        }
      }
    }

    if (catalogo != null) {
      catalogo.valoresCatalogos = valores;
    }

    return catalogo;
  }
}
