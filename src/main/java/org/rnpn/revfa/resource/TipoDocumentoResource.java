package org.rnpn.revfa.resource;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.rnpn.revfa.dto.*;
import org.rnpn.revfa.mapper.TipoDocumentoMapper;
import org.rnpn.revfa.optimized.service.TipoDocumentoOptimizedService;
import org.rnpn.revfa.service.TipoDocumentoService;

import java.util.List;

@Path("/tiposdocumentos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TipoDocumentoResource {

  @Inject
  TipoDocumentoService tipoDocumentoService;
  @Inject
  TipoDocumentoMapper tipoDocumentoMapper;
  @Inject
  TipoDocumentoOptimizedService tipoDocumentoOptimizedService;

  @GET
  @Path("/superior")
  public Uni<List<TipoDocumentoDTO>> getTiposDocumentosByIdSuperior(@QueryParam("id") String id) {
    return tipoDocumentoService.getTiposDocumentosByIdSuperior(id);
  }

  @GET
  @Path("/tiposDocumentos")
  public Uni<List<DocumentosDTO>> getTiposDocumentos() {
    return tipoDocumentoService.getArbolTiposDocumentos();
  }

  @GET
  @Path("/secciones")
  public Uni<List<SeccionesDTO>> getDocSecciones(@QueryParam("id") String id,
      @QueryParam("rol") String rol) {
    return tipoDocumentoService.seccionesAll(id, rol);
  }

  @GET
  @Path("/Stepper")
  public Uni<List<StepperDTO>> findSteper(@QueryParam("tipoSolicitud") Long tipoSolicitud) {
    return tipoDocumentoService.findSteper(tipoSolicitud);
  }

  @GET
  @Path("/seccionesPrueba")
  public Uni<List<SeccionesDTO>> getDocSeccionesPrueba(@QueryParam("id") String id,
      @QueryParam("rol") String rol) {
    return tipoDocumentoService.seccionesPrueba(id, rol);
  }

  @GET
  @Path("/solicitudCompleta")
  public Uni<List<SeccionesCompDTO>> getData(@QueryParam("id") String id,
      @QueryParam("rol") String rol, @QueryParam("solicitud") String solicitud) {
    return tipoDocumentoService.dataAll(id, rol, solicitud);
  }



  @GET
  @Path("/correlativo")
  public Uni<String> getDocSecciones(@QueryParam("id") Long id) {
    return tipoDocumentoService.Correlativo(id);
  }

  @GET
  @Path("/raiz")
  public Uni<TipoDocumentoDTO> encontrarRaizRecursivo(@QueryParam("id") Long id) {
    return tipoDocumentoService.encontrarRaizRecursivo(id).onItem()
        .transform(tipoDocumento -> tipoDocumentoMapper.toDto(tipoDocumento));
  }
}
