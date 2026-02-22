package com.service.api.idmhperu.dto.response;

public record FileDownload(String filename, String contentType, byte[] content) {}
