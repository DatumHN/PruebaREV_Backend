package org.rnpn.revfa.optimized.util;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.rnpn.revfa.optimized.service.TipoDocumentoOptimizedService;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PerformanceComparator {

  @Inject
  TipoDocumentoOptimizedService optimizedService;

  public Uni<Map<String, Object>> comparePerformance(String id, String rol) {
    // Test Entity Graph Implementation
    Instant startEntityGraph = Instant.now();
    return optimizedService.seccionesOptimized(id, rol).flatMap(entityGraphResult -> {
      Instant endEntityGraph = Instant.now();
      Duration entityGraphDuration = Duration.between(startEntityGraph, endEntityGraph);

      // Test BatchSize Implementation
      Instant startBatchSize = Instant.now();
      return optimizedService.seccionesBatched(id, rol).map(batchSizeResult -> {
        Instant endBatchSize = Instant.now();
        Duration batchSizeDuration = Duration.between(startBatchSize, endBatchSize);

        return Map
            .of("entity_graph",
                Map.of("duration_ms", entityGraphDuration.toMillis(), "result_size",
                    entityGraphResult.size(), "method", "Native SQL with aggregation"),
                "batch_size",
                Map.of("duration_ms", batchSizeDuration.toMillis(), "result_size",
                    batchSizeResult.size(), "method", "Native SQL with CTE"),
                "analysis",
                Map.of("fastest_method",
                    entityGraphDuration.toMillis() < batchSizeDuration.toMillis() ? "entity_graph"
                        : "batch_size",
                    "performance_improvement", "Both methods use optimized native SQL queries",
                    "timestamp", Instant.now().toString()));
      });
    });
  }

}
