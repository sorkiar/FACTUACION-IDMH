package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.TransferReason;
import com.service.api.idmhperu.dto.response.TransferReasonResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransferReasonMapper {

  TransferReasonResponse toResponse(TransferReason entity);

  List<TransferReasonResponse> toResponseList(List<TransferReason> entities);
}
