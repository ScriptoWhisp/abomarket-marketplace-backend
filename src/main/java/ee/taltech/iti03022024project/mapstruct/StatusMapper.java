package ee.taltech.iti03022024project.mapstruct;

import ee.taltech.iti03022024project.dto.StatusDto;
import ee.taltech.iti03022024project.domain.StatusEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StatusMapper {

    @Mapping(source = "statusId", target = "id")
    @Mapping(source = "statusName", target = "name")
    StatusDto toDto(StatusEntity statusEntity);

    @Mapping(source = "id", target = "statusId", ignore = true)
    @Mapping(source = "name", target = "statusName")
    StatusEntity toEntity(StatusDto statusDto);

}
