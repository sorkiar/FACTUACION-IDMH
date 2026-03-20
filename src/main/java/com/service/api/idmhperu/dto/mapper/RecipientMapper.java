package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.Recipient;
import com.service.api.idmhperu.dto.response.RecipientResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecipientMapper {

  RecipientResponse toResponse(Recipient entity);

  List<RecipientResponse> toResponseList(List<Recipient> entities);
}
