package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.PersonTypeFilter;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.PersonTypeResponse;
import java.util.List;

public interface PersonTypeService {
  ApiResponse<List<PersonTypeResponse>> findAll(PersonTypeFilter filter);
}
