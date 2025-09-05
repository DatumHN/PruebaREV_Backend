package org.rnpn.revfa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.rnpn.revfa.dto.TipoDocumentoDTO;
import org.rnpn.revfa.entity.TipoDocumento;

@Mapper(componentModel = "cdi") // Integración con Quarkus CDI
public interface TipoDocumentoMapper {

  TipoDocumentoDTO toDto(TipoDocumento entity);

  @Mapping(target = "secciones", ignore = true)
  @Mapping(target = "solicitudes", ignore = true)
  TipoDocumento toEntity(TipoDocumentoDTO dto);
}
