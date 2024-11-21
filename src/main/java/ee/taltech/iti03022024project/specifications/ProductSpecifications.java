package ee.taltech.iti03022024project.specifications;

import ee.taltech.iti03022024project.domain.ProductEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class ProductSpecifications {
    public static Specification<ProductEntity> hasId(Integer id) {
        return (root, query, criteriaBuilder) ->
                id == null ? null : criteriaBuilder.equal(root.get("id"), id);
    }


    public static Specification<ProductEntity> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<ProductEntity> hasSubstringInDescription(String subString) {
        return (root, query, criteriaBuilder) ->
                subString == null ? null : criteriaBuilder.like(root.get("description"), "%" + subString + "%");
    }

    public static Specification<ProductEntity> priceInRange(Double low, Double high) {
        String priceProperty = "price";

        return (root, query, criteriaBuilder) -> {
            if (low == null && high == null) {
                return null;
            } else if (low == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(priceProperty), high);
            } else if (high == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(priceProperty), low);
            } else {
                return criteriaBuilder.between(root.get(priceProperty), low, high);
            }
        };
    }

    public static Specification<ProductEntity> hasSeller(Integer sellerId) {
        return (root, query, criteriaBuilder) ->
                sellerId == null ? null : criteriaBuilder.equal(root.get("seller").get("id"), sellerId);
    }

    public static Specification<ProductEntity> leftAtLeast(Integer quantity) {
        return (root, query, criteriaBuilder) ->
                quantity == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("quantityInStock"), quantity);
    }


    public static Specification<ProductEntity> hasCategory(Integer categoryId) {
        return (root, query, criteriaBuilder) ->
                categoryId == null ? null : criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }


//    // idk if works
//    public static Specification<ProductEntity> inDateRange(long start, long end) {
//        return (root, query, criteriaBuilder) ->
//                start == 0 && end == 0 ? null : criteriaBuilder.between(root.get("dateAdded"), Instant.ofEpochMilli(start), Instant.ofEpochMilli(end));
//    }
}
