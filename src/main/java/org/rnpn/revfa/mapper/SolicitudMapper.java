package org.rnpn.revfa.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.rnpn.revfa.dto.SolicitudDTO;
import org.rnpn.revfa.entity.Solicitud;


@Mapper(componentModel = "cdi") // Integración con Quarkus CDI
public interface SolicitudMapper {
  SolicitudDTO toDto(Solicitud solicitud);

  Solicitud toEntity(SolicitudDTO solicitudDTO);

  List<SolicitudDTO> toDtos(List<Solicitud> detallesSolicitudes);

  List<Solicitud> toEntitys(List<SolicitudDTO> detallesSolicitudesDTOs);


}
