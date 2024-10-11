package ee.taltech.iti03022024project.mapstruct;

import ee.taltech.iti03022024project.controller.OrderItemDto;
import ee.taltech.iti03022024project.repository.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "order_item_id", target = "id")
    @Mapping(source = "order.order_id", target = "orderId")
    @Mapping(source = "product.product_id", target = "productId")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "price_at_time_of_order", target = "priceAtTimeOfOrder")
    OrderItemDto toDto(OrderItemEntity orderItemEntity);

    @Mapping(source = "id", target = "order_item_id", ignore = true)
    @Mapping(source = "orderId", target = "order")
    @Mapping(source = "productId", target = "product")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "priceAtTimeOfOrder", target = "price_at_time_of_order")
    OrderItemEntity toEntity(OrderItemDto orderItemDto);

    default OrderEntity mapOrderIdToOrderEntity(int orderId) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrder_id(orderId);
        return orderEntity;
    }

    default int mapOrderEntityToOrderId(OrderEntity order) {
        return order.getOrder_id();
    }

    default ProductEntity mapProductIdToProductEntity(int productId) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProduct_id(productId);
        return productEntity;
    }


    default int mapProductEntityToProductId(ProductEntity product) {
        return product.getProduct_id();
    }
}
