package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.Product;
import com.service.api.idmhperu.dto.filter.ProductFilter;
import com.service.api.idmhperu.dto.mapper.ProductMapper;
import com.service.api.idmhperu.dto.request.ProductRequest;
import com.service.api.idmhperu.dto.request.ProductStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ProductResponse;
import com.service.api.idmhperu.exception.BusinessValidationException;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.CategoryRepository;
import com.service.api.idmhperu.repository.ProductRepository;
import com.service.api.idmhperu.repository.UnitMeasureRepository;
import com.service.api.idmhperu.repository.spec.ProductSpecification;
import com.service.api.idmhperu.service.ProductService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
  private final ProductRepository repository;
  private final CategoryRepository categoryRepository;
  private final UnitMeasureRepository unitMeasureRepository;
  private final ProductMapper mapper;

  @Override
  public ApiResponse<List<ProductResponse>> findAll(ProductFilter filter) {
    return new ApiResponse<>(
        "Productos listados correctamente",
        mapper.toResponseList(repository.findAll(ProductSpecification.byFilter(filter)))
    );
  }

  @Override
  @Transactional
  public ApiResponse<ProductResponse> create(ProductRequest request) {

    if (repository.existsBySku(request.getSku())) {
      throw new BusinessValidationException("El SKU ya existe");
    }

    Product product = new Product();
    product.setSku(request.getSku());
    product.setName(request.getName());
    product.setCategory(categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada")));
    product.setUnitMeasure(unitMeasureRepository.findById(request.getUnitMeasureId())
        .orElseThrow(() -> new ResourceNotFoundException("Unidad de medida no encontrada")));
    product.setSalePrice(request.getSalePrice());
    product.setEstimatedCost(request.getEstimatedCost());
    product.setBrand(request.getBrand());
    product.setModel(request.getModel());
    product.setShortDescription(request.getShortDescription());
    product.setTechnicalSpec(request.getTechnicalSpec());
    product.setMainImageUrl(request.getMainImageUrl());
    product.setTechnicalSheetUrl(request.getTechnicalSheetUrl());
    product.setStatus(1);
    product.setCreatedBy("system");

    return new ApiResponse<>("Producto registrado correctamente",
        mapper.toResponse(repository.save(product)));
  }

  @Override
  @Transactional
  public ApiResponse<ProductResponse> update(Long id, ProductRequest request) {

    Product product = repository.findByIdAndStatusNot(id, 2)
        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

    product.setName(request.getName());
    product.setSalePrice(request.getSalePrice());
    product.setEstimatedCost(request.getEstimatedCost());
    product.setBrand(request.getBrand());
    product.setModel(request.getModel());
    product.setShortDescription(request.getShortDescription());
    product.setTechnicalSpec(request.getTechnicalSpec());
    product.setMainImageUrl(request.getMainImageUrl());
    product.setTechnicalSheetUrl(request.getTechnicalSheetUrl());
    product.setUpdatedBy("system");

    return new ApiResponse<>("Producto actualizado correctamente",
        mapper.toResponse(repository.save(product)));
  }

  @Override
  @Transactional
  public ApiResponse<Void> updateStatus(Long id, ProductStatusRequest request) {

    Product product = repository.findByIdAndStatusNot(id, 2)
        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

    product.setStatus(request.getStatus());
    product.setUpdatedBy("system");

    repository.save(product);
    return new ApiResponse<>("Estado del producto actualizado", null);
  }
}
