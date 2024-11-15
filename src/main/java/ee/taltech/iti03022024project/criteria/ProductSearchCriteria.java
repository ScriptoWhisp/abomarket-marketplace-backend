package ee.taltech.iti03022024project.criteria;

import ee.taltech.iti03022024project.domain.CategoryEntity;
import ee.taltech.iti03022024project.domain.UserEntity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;


import java.time.Instant;

public record ProductSearchCriteria(
        @Positive
        Integer productId,
        @NotEmpty
        String name,
        String description,
        @Positive
        Double price,
        @PositiveOrZero
        Integer quantityInStock,
        @NotNull
        UserEntity seller,
        @NotNull
        CategoryEntity category,
        @NotNull
        Long dateAddedMin,
        @NotNull
        Long dateAddedMax,
        String imageUrl
) {


}
