package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.filter.TransferReasonFilter;
import com.service.api.idmhperu.dto.mapper.TransferReasonMapper;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.TransferReasonResponse;
import com.service.api.idmhperu.repository.TransferReasonRepository;
import com.service.api.idmhperu.repository.spec.TransferReasonSpecification;
import com.service.api.idmhperu.service.TransferReasonService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferReasonServiceImpl implements TransferReasonService {

  private final TransferReasonRepository repository;
  private final TransferReasonMapper mapper;

  @Override
  public ApiResponse<List<TransferReasonResponse>> findAll(TransferReasonFilter filter) {
    return new ApiResponse<>("Motivos de traslado listados correctamente",
        mapper.toResponseList(repository.findAll(TransferReasonSpecification.byFilter(filter))));
  }
}
