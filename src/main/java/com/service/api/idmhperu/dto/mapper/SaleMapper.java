package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.Document;
import com.service.api.idmhperu.dto.entity.Sale;
import com.service.api.idmhperu.dto.entity.SaleItem;
import com.service.api.idmhperu.dto.response.DocumentResponse;
import com.service.api.idmhperu.dto.response.SaleItemResponse;
import com.service.api.idmhperu.dto.response.SaleResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleMapper {
  @Mapping(source = "client.id", target = "clientId")
  @Mapping(source = "client.businessName", target = "clientName")
  @Mapping(target = "document", expression = "java(mapLastDocument(entity.getDocuments()))")
  SaleResponse toResponse(Sale entity);

  List<SaleResponse> toResponseList(List<Sale> entities);

  SaleItemResponse toItemResponse(SaleItem entity);

  List<SaleItemResponse> toItemResponseList(List<SaleItem> entities);

  DocumentResponse toDocumentResponse(Document entity);

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
