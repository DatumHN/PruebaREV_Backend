package org.rnpn.revfa.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.rnpn.revfa.dto.DetalleSolicitudDTO;
import org.rnpn.revfa.entity.DetalleSolicitud;

@Mapper(componentModel = "cdiss") // Integración con Quarkus CDI
public interface DetalleSolicitudMapper {

  DetalleSolicitudDTO toDto(DetalleSolicitud detalleSolicitud);

  DetalleSolicitud toEntity(DetalleSolicitudDTO detalleSolicitudDTO);

  List<DetalleSolicitudDTO> toDtos(List<DetalleSolicitud> detallesSolicitudes);

  List<DetalleSolicitud> toEntitys(List<DetalleSolicitudDTO> detallesSolicitudesDTOs);
}
