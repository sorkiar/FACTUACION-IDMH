package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.Sale;
import com.service.api.idmhperu.dto.entity.SaleItem;
import com.service.api.idmhperu.dto.response.SaleItemResponse;
import com.service.api.idmhperu.dto.response.SaleResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleMapper {
  @Mapping(source = "client.id", target = "clientId")
  @Mapping(source = "client.businessName", target = "clientName")
  SaleResponse toResponse(Sale entity);

  List<SaleResponse> toResponseList(List<Sale> entities);

  SaleItemResponse toItemResponse(SaleItem entity);

  List<SaleItemResponse> toItemResponseList(List<SaleItem> entities);
}
