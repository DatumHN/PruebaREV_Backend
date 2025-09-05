package org.rnpn.revfa.resource;

import java.util.List;

import org.rnpn.revfa.dto.CampoDTO;
import org.rnpn.revfa.service.CampoService;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/campos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CampoResource {
  @Inject
  CampoService campoService;

  /*
   * @GET
   *
   * @Path("/seccion") public Uni<List<CampoDTO>> getCamposBySeccion(@QueryParam("id") String id) {
   * return campoService.getCamposBySeccion(id); }
   */
  @GET
  @Path("/campos")
  public Uni<List<CampoDTO>> getCamposBySeccion2(@QueryParam("id") String id) {
    return campoService.getCamposBySeccion2(id);
  }
}
