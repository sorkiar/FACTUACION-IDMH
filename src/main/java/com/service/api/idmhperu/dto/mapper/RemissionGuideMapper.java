package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.RemissionGuide;
import com.service.api.idmhperu.dto.entity.RemissionGuideDriver;
import com.service.api.idmhperu.dto.entity.RemissionGuideItem;
import com.service.api.idmhperu.dto.response.RemissionGuideDriverResponse;
import com.service.api.idmhperu.dto.response.RemissionGuideItemResponse;
import com.service.api.idmhperu.dto.response.RemissionGuideResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RemissionGuideMapper {

  @Mapping(target = "documentSeriesId", source = "documentSeries.id")
  @Mapping(target = "items", source = "items")
  @Mapping(target = "drivers", source = "drivers")
  RemissionGuideResponse toResponse(RemissionGuide entity);

  List<RemissionGuideResponse> toResponseList(List<RemissionGuide> entities);

  @Mapping(target = "productId", source = "product.id")
  RemissionGuideItemResponse toItemResponse(RemissionGuideItem entity);

  RemissionGuideDriverResponse toDriverResponse(RemissionGuideDriver entity);
}
