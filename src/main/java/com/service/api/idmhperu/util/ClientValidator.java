package com.service.api.idmhperu.util;

import com.service.api.idmhperu.dto.entity.PersonType;
import com.service.api.idmhperu.dto.request.ClientRequest;
import com.service.api.idmhperu.exception.BusinessValidationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ClientValidator {
  public void validateByPersonType(PersonType personType, ClientRequest request) {
    if ("Persona Natural".equalsIgnoreCase(personType.getName())) {
      if (!StringUtils.hasText(request.getFirstName()))
        throw new BusinessValidationException("El nombre es obligatorio para persona natural");

      if (!StringUtils.hasText(request.getLastName()))
        throw new BusinessValidationException("El apellido es obligatorio para persona natural");
    }

    if ("Persona Jurídica".equalsIgnoreCase(personType.getName())) {
      if (!StringUtils.hasText(request.getBusinessName()))
        throw new BusinessValidationException("La razón social es obligatoria");

      if (!StringUtils.hasText(request.getContactPersonName()))
        throw new BusinessValidationException("El nombre del contacto es obligatorio");
    }
  }
}
