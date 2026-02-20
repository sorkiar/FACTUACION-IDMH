package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.Client;
import com.service.api.idmhperu.dto.entity.Document;
import com.service.api.idmhperu.dto.entity.PaymentMethod;
import com.service.api.idmhperu.dto.entity.Sale;
import com.service.api.idmhperu.dto.entity.SaleItem;
import com.service.api.idmhperu.dto.entity.SalePayment;
import com.service.api.idmhperu.dto.response.ClientResponse;
import com.service.api.idmhperu.dto.response.DocumentResponse;
import com.service.api.idmhperu.dto.response.PaymentMethodResponse;
import com.service.api.idmhperu.dto.response.SaleItemResponse;
import com.service.api.idmhperu.dto.response.SalePaymentResponse;
import com.service.api.idmhperu.dto.response.SaleResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleMapper {
  @Mapping(target = "document", expression = "java(mapLastDocument(entity.getDocuments()))")
  SaleResponse toResponse(Sale entity);

  List<SaleResponse> toResponseList(List<Sale> entities);

  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "serviceId", source = "service.id")
  SaleItemResponse toItemResponse(SaleItem entity);

  List<SaleItemResponse> toItemResponseList(List<SaleItem> entities);

  DocumentResponse toDocumentResponse(Document entity);

  @Mapping(target = "personTypeId", source = "personType.id")
  @Mapping(target = "personType", source = "personType.name")
  @Mapping(target = "documentTypeId", source = "documentType.id")
  @Mapping(target = "documentType", source = "documentType.name")
  @Mapping(target = "birthDate", source = "birthDate", dateFormat = "yyyy-MM-dd")
  ClientResponse toClientResponse(Client entity);

  PaymentMethodResponse toPaymentMethodResponse(PaymentMethod entity);

  @Mapping(target = "paymentMethod", source = "paymentMethod")
  SalePaymentResponse toPaymentResponse(SalePayment entity);

  List<SalePaymentResponse> toPaymentResponseList(List<SalePayment> entities);

  default DocumentResponse mapLastDocument(Set<Document> documents) {
    if (documents == null || documents.isEmpty()) {
      return null;
    }

    return documents.stream()
        .filter(doc -> doc.getDocumentTypeSunat() != null
                && doc.getDocumentTypeSunat().getCode() != null
                && (
                "01".equals(doc.getDocumentTypeSunat().getCode()) ||
                    "03".equals(doc.getDocumentTypeSunat().getCode())
            )
        )
        .max(Comparator.comparing(Document::getCreatedAt))
        .map(this::toDocumentResponse)
        .orElse(null);
  }

}
