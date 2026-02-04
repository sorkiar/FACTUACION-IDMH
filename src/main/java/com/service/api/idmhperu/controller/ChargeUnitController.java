package com.service.api.idmhperu.controller;

import com.service.api.idmhperu.dto.filter.ChargeUnitFilter;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ChargeUnitResponse;
import com.service.api.idmhperu.service.ChargeUnitService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/charge-units")
@RequiredArgsConstructor
public class ChargeUnitController {
  private final ChargeUnitService service;

  @GetMapping
  public ApiResponse<List<ChargeUnitResponse>> list(
      @RequestParam(required = false) Integer status
  ) {
    ChargeUnitFilter filter = new ChargeUnitFilter();
    filter.setStatus(status);

    return service.findAll(filter);
  }
}
