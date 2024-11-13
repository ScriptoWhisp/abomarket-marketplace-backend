package ee.taltech.iti03022024project.criteria;

import ee.taltech.iti03022024project.domain.CategoryEntity;
import ee.taltech.iti03022024project.domain.UserEntity;


import java.time.Instant;

public record ProductSearchCriteria(
        int productId,
        String name,
        String description,
        double price, int quantityInStock,
        UserEntity seller,
        CategoryEntity category,
        Instant dateAdded,
        String imageUrl
) {


}
