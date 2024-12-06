package ee.taltech.iti03022024project.specifications;

import ee.taltech.iti03022024project.domain.OrderEntity;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecifications {

    private OrderSpecifications() {
    }

    public static Specification<OrderEntity> hasId(Integer id) {
        return (root, query, criteriaBuilder) ->
                id == null ? null : criteriaBuilder.equal(root.get("id"), id);
    }

    public static Specification<OrderEntity> hasStatusId(Integer statusId) {
        return (root, query, criteriaBuilder) ->
                statusId == null ? null : criteriaBuilder.equal(root.get("status").get("statusId"), statusId);
    }

    public static Specification<OrderEntity> hasUserId(Integer userId) {
        return (root, query, criteriaBuilder) ->
                userId == null ? null : criteriaBuilder.equal(root.get("user").get("userId"), userId);
    }

}
