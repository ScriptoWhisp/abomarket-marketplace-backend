package ee.taltech.iti03022024project.mapstruct;

import ee.taltech.iti03022024project.controller.UserDto;
import ee.taltech.iti03022024project.repository.UserEntity;
import jakarta.persistence.Id;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // first name and last name are combined into name
    @Mapping(source = "user_id", target = "id")
    @Mapping(source = "first_name", target = "firstName")
    @Mapping(source = "last_name", target = "lastName")
    UserDto toDto(UserEntity userEntity);


    @Mapping(source = "id", target = "user_id", ignore = true)
    @Mapping(source = "firstName", target = "first_name")
    @Mapping(source = "lastName", target = "last_name")
    UserEntity toEntity(UserDto userDto);

}