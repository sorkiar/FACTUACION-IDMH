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
import com.service.api.idmhperu.service.GoogleDriveService;
import com.service.api.idmhperu.service.ProductService;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
  private final ProductRepository repository;
  private final CategoryRepository categoryRepository;
  private final UnitMeasureRepository unitMeasureRepository;
  private final ProductMapper mapper;
  private final GoogleDriveService googleDriveService;

  // IDs reales de carpetas en Drive
  private static final String PRODUCT_IMAGE_FOLDER_ID = "1vrVeXcPjUJyuj7ge2LBELuOVmH6FJefP";
  private static final String PRODUCT_TECH_SHEET_FOLDER_ID = "1CsUe2qaZKXsBcReW8McCeR7wG8_IBmt6";

  @Override
  public ApiResponse<List<ProductResponse>> findAll(ProductFilter filter) {
    return new ApiResponse<>(
        "Productos listados correctamente",
        mapper.toResponseList(repository.findAll(ProductSpecification.byFilter(filter)))
    );
  }

  @Override
  @Transactional
  public ApiResponse<ProductResponse> create(
      ProductRequest request,
      MultipartFile mainImage,
      MultipartFile technicalSheet
  ) {

    if (repository.existsBySku(request.getSku())) {
      throw new BusinessValidationException("El SKU ya existe");
    }

    if (mainImage == null || mainImage.isEmpty()) {
      throw new BusinessValidationException("La imagen principal es obligatoria");
    }

    System.out.println("MAIN IMAGE: " + mainImage.getOriginalFilename());
    System.out.println("SIZE: " + mainImage.getSize());
    System.out.println("CONTENT TYPE: " + mainImage.getContentType());

    try {
      File imageFile = convertMultipartToFile(
          mainImage,
          request.getSku() + "_image"
      );

      String imageUrl = googleDriveService.uploadFileWithPublicAccess(
          imageFile,
          PRODUCT_IMAGE_FOLDER_ID
      );

      String technicalSheetUrl = null;

      if (technicalSheet != null && !technicalSheet.isEmpty()) {
        File pdfFile = convertMultipartToFile(
            technicalSheet,
            request.getSku() + "_tech_sheet"
        );

        String fileId = googleDriveService.uploadPdf(
            pdfFile,
            PRODUCT_TECH_SHEET_FOLDER_ID
        );

        technicalSheetUrl =
            "https://drive.google.com/file/d/" + fileId + "/view";
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
      product.setMainImageUrl(imageUrl);
      product.setTechnicalSheetUrl(technicalSheetUrl);
      product.setStatus(1);
      product.setCreatedBy("system");

      return new ApiResponse<>(
          "Producto registrado correctamente",
          mapper.toResponse(repository.save(product))
      );

    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessValidationException(
          "Error al subir archivos a Google Drive: " + e.getMessage()
      );
    }
  }

  @Override
  @Transactional
  public ApiResponse<ProductResponse> update(
      Long id,
      ProductRequest request,
      MultipartFile mainImage,
      MultipartFile technicalSheet
  ) {
    Product product = repository.findByIdAndStatusNot(id, 2)
        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

    try {
      if (mainImage != null && !mainImage.isEmpty()) {
        File imageFile = convertMultipartToFile(
            mainImage,
            product.getSku() + "_image"
        );

        String imageUrl = googleDriveService.uploadFileWithPublicAccess(
            imageFile,
            PRODUCT_IMAGE_FOLDER_ID
        );

        product.setMainImageUrl(imageUrl);
      }

      if (technicalSheet != null && !technicalSheet.isEmpty()) {
        File pdfFile = convertMultipartToFile(
            technicalSheet,
            product.getSku() + "_tech_sheet"
        );

        String fileId = googleDriveService.uploadPdf(
            pdfFile,
            PRODUCT_TECH_SHEET_FOLDER_ID
        );

        String technicalSheetUrl =
            "https://drive.google.com/file/d/" + fileId + "/view";

        product.setTechnicalSheetUrl(technicalSheetUrl);
      }

      product.setName(request.getName());
      product.setSalePrice(request.getSalePrice());
      product.setEstimatedCost(request.getEstimatedCost());
      product.setBrand(request.getBrand());
      product.setModel(request.getModel());
      product.setShortDescription(request.getShortDescription());
      product.setTechnicalSpec(request.getTechnicalSpec());
      product.setUpdatedBy("system");

      return new ApiResponse<>(
          "Producto actualizado correctamente",
          mapper.toResponse(repository.save(product))
      );

    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessValidationException(
          "Error al actualizar archivos del producto: " + e.getMessage()
      );
    }
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

  private File convertMultipartToFile(
      MultipartFile multipart,
      String prefix
  ) throws IOException {

    File file = File.createTempFile(prefix, "_" + multipart.getOriginalFilename());
    multipart.transferTo(file);
    return file;
  }
}
