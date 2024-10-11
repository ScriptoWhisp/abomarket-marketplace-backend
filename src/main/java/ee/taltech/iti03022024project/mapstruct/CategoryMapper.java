package ee.taltech.iti03022024project.mapstruct;

import ee.taltech.iti03022024project.controller.CategoryDto;
import ee.taltech.iti03022024project.repository.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "categoryId", target = "id")
    @Mapping(source = "categoryName", target = "name")
    CategoryDto toDto(CategoryEntity categoryEntity);

    @Mapping(source = "id", target = "categoryId", ignore = true)
    @Mapping(source = "name", target = "categoryName")
    CategoryEntity toEntity(CategoryDto categoryDto);

}