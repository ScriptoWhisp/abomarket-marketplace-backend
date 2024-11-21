package ee.taltech.iti03022024project.criteria;

import ee.taltech.iti03022024project.domain.CategoryEntity;
import ee.taltech.iti03022024project.domain.UserEntity;
import jakarta.validation.constraints.*;


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
        @Positive
        Long dateAddedMin,
        @Positive
        Long dateAddedMax,
        @Pattern(regexp = "ASC|DESC", flags = Pattern.Flag.CASE_INSENSITIVE)
        String sortDirection,
        String imageUrl
) {


}
