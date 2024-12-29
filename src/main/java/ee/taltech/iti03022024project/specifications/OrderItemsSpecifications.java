package ee.taltech.iti03022024project.specifications;

import ee.taltech.iti03022024project.domain.OrderItemEntity;
import org.springframework.data.jpa.domain.Specification;

public class OrderItemsSpecifications {

    private OrderItemsSpecifications() {
    }

    public static Specification<OrderItemEntity> hasId(Integer id) {
        return (root, query, criteriaBuilder) ->
                id == null ? null : criteriaBuilder.equal(root.get("id"), id);
    }

    public static Specification<OrderItemEntity> hasOrderId(Integer orderId) {
        return (root, query, criteriaBuilder) ->
                orderId == null ? null : criteriaBuilder.equal(root.get("order").get("orderId"), orderId);
    }

    public static Specification<OrderItemEntity> hasProductId(Integer productId) {
        return (root, query, criteriaBuilder) ->
                productId == null ? null : criteriaBuilder.equal(root.get("product").get("productId"), productId);
    }

    public static Specification<OrderItemEntity> hasQuantity(Integer quantity) {
        return (root, query, criteriaBuilder) ->
                quantity == null ? null : criteriaBuilder.equal(root.get("quantity"), quantity);
    }

    public static Specification<OrderItemEntity> hasPriceAtTimeOfOrder(Double priceAtTimeOfOrder) {
        return (root, query, criteriaBuilder) ->
                priceAtTimeOfOrder == null ? null : criteriaBuilder.equal(root.get("priceAtTimeOfOrder"), priceAtTimeOfOrder);
    }

}
