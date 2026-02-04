package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.User;
import com.service.api.idmhperu.dto.response.UserResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
  @Mapping(source = "documentType.id", target = "documentTypeId")
  @Mapping(source = "documentType.name", target = "documentTypeName")
  @Mapping(source = "profile.id", target = "profileId")
  @Mapping(source = "profile.name", target = "profileName")
  UserResponse toResponse(User entity);
  List<UserResponse> toResponseList(List<User> entities);
}
