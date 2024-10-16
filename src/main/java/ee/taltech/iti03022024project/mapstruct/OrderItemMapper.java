package ee.taltech.iti03022024project.mapstruct;

import ee.taltech.iti03022024project.dto.OrderItemDto;
import ee.taltech.iti03022024project.domain.OrderEntity;
import ee.taltech.iti03022024project.domain.OrderItemEntity;
import ee.taltech.iti03022024project.domain.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "orderItemId", target = "id")
    @Mapping(source = "order.orderId", target = "orderId")
    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "priceAtTimeOfOrder", target = "priceAtTimeOfOrder")
    OrderItemDto toDto(OrderItemEntity orderItemEntity);

    @Mapping(source = "id", target = "orderItemId", ignore = true)
    @Mapping(source = "orderId", target = "order")
    @Mapping(source = "productId", target = "product")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "priceAtTimeOfOrder", target = "priceAtTimeOfOrder")
    OrderItemEntity toEntity(OrderItemDto orderItemDto);

    default OrderEntity mapOrderIdToOrderEntity(int orderId) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(orderId);
        return orderEntity;
    }

    default int mapOrderEntityToOrderId(OrderEntity order) {
        return order.getOrderId();
    }

    default ProductEntity mapProductIdToProductEntity(int productId) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductId(productId);
        return productEntity;
    }


    default int mapProductEntityToProductId(ProductEntity product) {
        return product.getProductId();
    }
}
