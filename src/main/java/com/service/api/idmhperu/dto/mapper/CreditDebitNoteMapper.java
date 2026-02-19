package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.CreditDebitNote;
import com.service.api.idmhperu.dto.entity.CreditDebitNoteItem;
import com.service.api.idmhperu.dto.entity.CreditDebitNoteType;
import com.service.api.idmhperu.dto.response.CreditDebitNoteItemResponse;
import com.service.api.idmhperu.dto.response.CreditDebitNoteResponse;
import com.service.api.idmhperu.dto.response.CreditDebitNoteTypeResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SaleMapper.class})
public interface CreditDebitNoteMapper {

  @Mapping(target = "sale", source = "sale")
  @Mapping(target = "originalDocument", source = "originalDocument")
  @Mapping(target = "creditDebitNoteType", source = "creditDebitNoteType")
  @Mapping(target = "items", source = "items")
  CreditDebitNoteResponse toResponse(CreditDebitNote entity);

  List<CreditDebitNoteResponse> toResponseList(List<CreditDebitNote> entities);

  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "serviceId", source = "service.id")
  CreditDebitNoteItemResponse toItemResponse(CreditDebitNoteItem entity);

  CreditDebitNoteTypeResponse toTypeResponse(CreditDebitNoteType entity);
}
