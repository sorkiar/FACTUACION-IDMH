package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.Product;
import com.service.api.idmhperu.dto.response.ProductResponse;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {
  @Mapping(target = "categoryId", source = "category.id")
  @Mapping(target = "categoryName", source = "category.name")
  @Mapping(target = "unitMeasureId", source = "unitMeasure.id")
  @Mapping(target = "unitMeasureCode", source = "unitMeasure.code")
  ProductResponse toResponse(Product entity);
  List<ProductResponse> toResponseList(List<Product> entities);
}
