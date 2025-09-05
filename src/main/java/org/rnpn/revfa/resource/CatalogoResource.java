package org.rnpn.revfa.resource;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.rnpn.revfa.dto.CatalogosDTO;
import org.rnpn.revfa.mapper.CatalogoMapper;
import org.rnpn.revfa.service.CatalogoService;

@Path("/catalogo")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CatalogoResource {

  @Inject
  CatalogoService catalogoService;
  @Inject
  CatalogoMapper catalogoMapper;

  @GET
  @Path("/catalogoHijo")
  public Uni<CatalogosDTO> getTiposDocumentosByIdSuperior(@QueryParam("id") Long id) {
    return catalogoService.getCatalogoHijo(id).flatMap(catalogo -> {
      CatalogosDTO dto = catalogoMapper.toDto(catalogo);
      return dto.recursivo(dto.id).map(resultado -> {
        dto.subCatalogo = resultado;
        return dto;
      });
    });
  }

}
