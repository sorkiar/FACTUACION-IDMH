package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.Client;
import com.service.api.idmhperu.dto.request.ClientRequest;
import com.service.api.idmhperu.dto.response.ClientResponse;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ClientMapper {
  /* ============================
     CREATE
     ============================ */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "personType", ignore = true)
  @Mapping(target = "documentType", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deletedBy", ignore = true)
  Client toEntity(ClientRequest request);

  /* ============================
     UPDATE (PUT)
     ============================ */
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "personType", ignore = true)
  @Mapping(target = "documentType", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deletedBy", ignore = true)
  void updateEntity(@MappingTarget Client entity, ClientRequest request);

  /* ============================
     RESPONSE
     ============================ */
  @Mapping(target = "personTypeId", source = "personType.id")
  @Mapping(target = "personType", source = "personType.name")
  @Mapping(target = "documentTypeId", source = "documentType.id")
  @Mapping(target = "documentType", source = "documentType.name")
  ClientResponse toResponse(Client client);

  List<ClientResponse> toResponseList(List<Client> clients);
}
