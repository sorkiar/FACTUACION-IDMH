package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.ClientFilter;
import com.service.api.idmhperu.dto.request.ClientRequest;
import com.service.api.idmhperu.dto.request.ClientStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ClientResponse;
import java.util.List;

public interface ClientService {
  ApiResponse<List<ClientResponse>> findAll(ClientFilter filter);

  ApiResponse<ClientResponse> create(ClientRequest request);

  ApiResponse<ClientResponse> update(Long id, ClientRequest request);

  ApiResponse<Void> updateStatus(Long id, ClientStatusRequest request);
}
