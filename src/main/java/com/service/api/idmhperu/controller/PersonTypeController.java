package com.service.api.idmhperu.controller;

import com.service.api.idmhperu.dto.filter.PersonTypeFilter;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.PersonTypeResponse;
import com.service.api.idmhperu.service.PersonTypeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/person-types")
@RequiredArgsConstructor
public class PersonTypeController {
  private final PersonTypeService service;

  @GetMapping
  public ApiResponse<List<PersonTypeResponse>> list(
      @RequestParam(required = false) Integer status
  ) {
    PersonTypeFilter filter = new PersonTypeFilter();
    filter.setStatus(status);

    return service.findAll(filter);
  }
}
