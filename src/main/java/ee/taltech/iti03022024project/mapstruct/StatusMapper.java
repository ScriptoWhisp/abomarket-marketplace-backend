package ee.taltech.iti03022024project.mapstruct;

import ee.taltech.iti03022024project.controller.StatusDto;
import ee.taltech.iti03022024project.repository.StatusEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StatusMapper {

    @Mapping(source = "status_id", target = "id")
    @Mapping(source = "status_name", target = "name")
    StatusDto toDto(StatusEntity statusEntity);

    @Mapping(source = "id", target = "status_id", ignore = true)
    @Mapping(source = "name", target = "status_name")
    StatusEntity toEntity(StatusDto statusDto);

}
