package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.ClientAddress;
import com.service.api.idmhperu.dto.response.ClientAddressResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientAddressMapper {

  ClientAddressResponse toResponse(ClientAddress entity);

  List<ClientAddressResponse> toResponseList(List<ClientAddress> entities);
}
