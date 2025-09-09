package org.rnpn.revfa.optimized.resource;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.rnpn.revfa.optimized.dto.CatalogoDTOOptimized;
import org.rnpn.revfa.optimized.service.CatalogoOptimizedService;

@Path("/catalogo-optimized")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CatalogoOptimizedResource {

  @Inject
  CatalogoOptimizedService catalogoOptimizedService;

  @GET
  @Path("/catalogoHijo")
  public Uni<CatalogoDTOOptimized> getCatalogoHijo(@QueryParam("idSuperior") Long idSuperior,
      @QueryParam("catalogoId") Long catalogoId) {
    return catalogoOptimizedService.getCatalogoHijo(idSuperior, catalogoId).flatMap(catalogo -> {
      if (catalogo == null) {
        return Uni.createFrom().nullItem();
      }

      // Check if it has subcatalogos and set the flag
      return catalogoOptimizedService.hasSubCatalogos(catalogo.id).map(hasSubCatalogos -> {
        catalogo.subCatalogo = hasSubCatalogos;
        return catalogo;
      });
    });
  }
}
