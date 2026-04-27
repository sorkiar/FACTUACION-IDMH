package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.Profile;
import com.service.api.idmhperu.dto.response.ProfileResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

  ProfileResponse toResponse(Profile entity);

  List<ProfileResponse> toResponseList(List<Profile> entities);
}
