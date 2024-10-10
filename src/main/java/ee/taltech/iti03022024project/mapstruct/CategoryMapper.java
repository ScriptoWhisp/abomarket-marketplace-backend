package ee.taltech.iti03022024project.mapstruct;

import ee.taltech.iti03022024project.controller.CategoryDto;
import ee.taltech.iti03022024project.repository.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "category_id", target = "id")
    @Mapping(source = "category_name", target = "name")
    CategoryDto toDto(CategoryEntity categoryEntity);

    @Mapping(source = "id", target = "category_id", ignore = true)
    @Mapping(source = "name", target = "category_name")
    CategoryEntity toEntity(CategoryDto categoryDto);

}