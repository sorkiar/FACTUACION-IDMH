package com.service.api.idmhperu.dto.external.apiperu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExternalRucData {

  @JsonProperty("ruc")
  private String ruc;

  @JsonProperty("nombre_o_razon_social")
  private String name;

  @JsonProperty("estado_del_contribuyente")
  private String state;

  @JsonProperty("condicion_de_domicilio")
  private String condition;

  @JsonProperty("direccion")
  private String address;

  @JsonProperty("direccion_completa")
  private String fullAddress;

  @JsonProperty("departamento")
  private String department;

  @JsonProperty("provincia")
  private String province;

  @JsonProperty("distrito")
  private String district;

  @JsonProperty("ubigeo_sunat")
  private String ubigeoSunat;

  @JsonProperty("ubigeo")
  private String[] ubigeo;

  @JsonProperty("es_agente_de_retencion")
  private Boolean isRetentionAgent;

  @JsonProperty("es_agente_de_percepcion")
  private Boolean isPerceptionAgent;

  @JsonProperty("es_agente_de_percepcion_combustible")
  private Boolean isPerceptionFuelAgent;

  @JsonProperty("es_buen_contribuyente")
  private Boolean isGoodTaxpayer;
}
