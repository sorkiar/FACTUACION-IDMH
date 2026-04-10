package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.ClientFilter;
import com.service.api.idmhperu.dto.request.ClientAddressRequest;
import com.service.api.idmhperu.dto.request.ClientRequest;
import com.service.api.idmhperu.dto.request.ClientStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ClientAddressResponse;
import com.service.api.idmhperu.dto.response.ClientResponse;
import java.util.List;

public interface ClientService {

  ApiResponse<List<ClientResponse>> findAll(ClientFilter filter);

  ApiResponse<ClientResponse> findById(Long id);

  ApiResponse<ClientResponse> create(ClientRequest request);

  ApiResponse<ClientResponse> update(Long id, ClientRequest request);

  ApiResponse<Void> updateStatus(Long id, ClientStatusRequest request);

  // Address management
  ApiResponse<List<ClientAddressResponse>> findAddresses(Long clientId);

  ApiResponse<ClientAddressResponse> addAddress(Long clientId, ClientAddressRequest request);

  ApiResponse<ClientAddressResponse> updateAddress(Long clientId, Long addressId,
      ClientAddressRequest request);

  ApiResponse<Void> deleteAddress(Long clientId, Long addressId);
}
