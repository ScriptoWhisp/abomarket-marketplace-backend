package ee.taltech.iti03022024project.mapstruct;

import ee.taltech.iti03022024project.controller.ProductDto;
import ee.taltech.iti03022024project.controller.UserDto;
import ee.taltech.iti03022024project.repository.ProductEntity;
import ee.taltech.iti03022024project.repository.UserEntity;
import jakarta.persistence.Id;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // first name and last name are combined into name
    @Mapping(source = "product_id", target = "id")
    @Mapping(source = "quantity_in_stock", target = "stockQuantity")
    @Mapping(source = "seller.user_id", target = "sellerId")
    @Mapping(source = "category_id", target = "categoryId")
    @Mapping(source = "date_added", target = "dateAdded")
    ProductDto toDto(ProductEntity productEntity);


    @Mapping(source = "id", target = "product_id", ignore = true)
    @Mapping(source = "stockQuantity", target = "quantity_in_stock")
    @Mapping(source = "sellerId", target = "seller")
    @Mapping(source = "categoryId", target = "category_id")
    @Mapping(source = "dateAdded", target = "date_added", ignore = true)
    ProductEntity toEntity(ProductDto productDto);


    // Helper method to map sellerId to UserEntity when converting DTO to entity
    default UserEntity mapSellerIdToUserEntity(int sellerId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUser_id(sellerId);
        return userEntity;
    }

    // Helper method to map UserEntity to sellerId when converting entity to DTO
    default int mapUserEntityToSellerId(UserEntity seller) {
        return seller.getUser_id();
    }
}