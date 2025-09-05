package org.rnpn.revfa.mapper;

import org.mapstruct.Mapper;
import org.rnpn.revfa.dto.CatalogosDTO;
import org.rnpn.revfa.entity.Catalogos;

import java.util.List;


@Mapper(componentModel = "cdi") // Integración con Quarkus CDI
public interface CatalogoMapper {
  CatalogosDTO toDto(Catalogos catalogos);

  Catalogos toEntity(CatalogosDTO catalogosDTO);

  List<CatalogosDTO> toDtos(List<Catalogos> detallesSolicitudes);

  List<Catalogos> toEntitys(List<CatalogosDTO> detallesSolicitudesDTOs);


}
