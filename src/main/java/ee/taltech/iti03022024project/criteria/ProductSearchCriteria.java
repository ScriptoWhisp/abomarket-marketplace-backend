package ee.taltech.iti03022024project.criteria;

import ee.taltech.iti03022024project.domain.CategoryEntity;
import ee.taltech.iti03022024project.domain.UserEntity;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record ProductSearchCriteria(
        @Positive
        Integer productId,
        @Size(max = 255)
        String name,
        String description,
        @Positive
        Double price,
        @PositiveOrZero
        Integer quantityInStock,
        UserEntity seller,
        CategoryEntity category,
        @Pattern(regexp = "ASC|DESC")
        String sortDirection,
        @Pattern(regexp = "price|name|productId")
        String sortBy,
        String imageUrl
) {


}
