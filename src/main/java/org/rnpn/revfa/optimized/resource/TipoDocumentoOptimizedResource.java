package org.rnpn.revfa.optimized.resource;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.rnpn.revfa.optimized.dto.SeccionesDTOOptimized;
import org.rnpn.revfa.optimized.service.TipoDocumentoOptimizedService;
import org.rnpn.revfa.optimized.util.PerformanceComparator;

import java.util.List;
import java.util.Map;

@Path("/optimized/tipodocumentos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TipoDocumentoOptimizedResource {

  @Inject
  TipoDocumentoOptimizedService tipoDocumentoOptimizedService;

  @Inject
  PerformanceComparator performanceComparator;

  @GET
  @Path("/{id}/secciones/entity-graph")
  public Uni<List<SeccionesDTOOptimized>> seccionesWithEntityGraph(@PathParam("id") String id,
      @QueryParam("rol") String rol) {
    return tipoDocumentoOptimizedService.seccionesOptimized(id, rol);
  }

  @GET
  @Path("/{id}/seccionesCompl/entity-graph")
  public Uni<List<SeccionesDTOOptimized>> seccionesCompWithEntityGraphseccionesCompWithEntityGraph(
      @PathParam("id") String id, @QueryParam("rol") String rol,
      @QueryParam("idSolicitud") String idSolicitud) {
    return tipoDocumentoOptimizedService.seccionesCompleteOptimized(id, rol, idSolicitud);
  }

  @GET
  @Path("/{id}/secciones/batch-size")
  public Uni<List<SeccionesDTOOptimized>> seccionesWithBatchSize(@PathParam("id") String id,
      @QueryParam("rol") String rol) {
    return tipoDocumentoOptimizedService.seccionesBatched(id, rol);
  }

  @GET
  @Path("/{id}/performance-comparison")
  public Uni<Map<String, Object>> comparePerformance(@PathParam("id") String id,
      @QueryParam("rol") String rol) {
    return performanceComparator.comparePerformance(id, rol);
  }
}
