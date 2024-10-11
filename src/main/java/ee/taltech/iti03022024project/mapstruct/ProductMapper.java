package ee.taltech.iti03022024project.mapstruct;

import ee.taltech.iti03022024project.controller.ProductDto;
import ee.taltech.iti03022024project.repository.CategoryEntity;
import ee.taltech.iti03022024project.repository.ProductEntity;
import ee.taltech.iti03022024project.repository.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // first name and last name are combined into name
    @Mapping(source = "productId", target = "id")
    @Mapping(source = "quantityInStock", target = "stockQuantity")
    @Mapping(source = "seller.userId", target = "sellerId")
    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "dateAdded", target = "dateAdded")
    ProductDto toDto(ProductEntity productEntity);


    @Mapping(source = "id", target = "productId", ignore = true)
    @Mapping(source = "stockQuantity", target = "quantityInStock")
    @Mapping(source = "sellerId", target = "seller")
    @Mapping(source = "categoryId", target = "category")
    @Mapping(source = "dateAdded", target = "dateAdded", ignore = true)
    ProductEntity toEntity(ProductDto productDto);


    // Helper method to map sellerId to UserEntity when converting DTO to entity
    default UserEntity mapSellerIdToUserEntity(int sellerId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(sellerId);
        return userEntity;
    }

    // Helper method to map UserEntity to sellerId when converting entity to DTO
    default int mapUserEntityToSellerId(UserEntity seller) {
        return seller.getUserId();
    }

    // Helper method to map categoryId to CategoryEntity when converting DTO to entity
    default CategoryEntity mapCategoryIdToCategoryEntity(int categoryId) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setCategoryId(categoryId);
        return categoryEntity;
    }

    // Helper method to map CategoryEntity to categoryId when converting entity to DTO
    default int mapCategoryEntityToCategoryId(CategoryEntity category) {
        return category.getCategoryId();
    }
}