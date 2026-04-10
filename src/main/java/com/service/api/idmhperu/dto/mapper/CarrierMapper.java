package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.Carrier;
import com.service.api.idmhperu.dto.response.CarrierResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarrierMapper {

  CarrierResponse toResponse(Carrier entity);

  List<CarrierResponse> toResponseList(List<Carrier> entities);
}
