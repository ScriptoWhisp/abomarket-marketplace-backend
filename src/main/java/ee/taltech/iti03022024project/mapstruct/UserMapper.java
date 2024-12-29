package ee.taltech.iti03022024project.mapstruct;

import ee.taltech.iti03022024project.domain.OrderEntity;
import ee.taltech.iti03022024project.domain.UserEntity;
import ee.taltech.iti03022024project.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // first name and last name are combined into name
    @Mapping(source = "userId", target = "id")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "unfinishedOrder.orderId", target = "unfinishedOrderId")
    UserDto toDto(UserEntity userEntity);


    @Mapping(source = "id", target = "userId", ignore = true)
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "unfinishedOrderId", target = "unfinishedOrder")
    @Mapping(target = "createdAt", ignore = true)
    UserEntity toEntity(UserDto userDto);

    default OrderEntity mapUnfinishedOrderIdToOrderEntity(int orderId) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(orderId);
        return orderEntity;
    }

    default int mapUserEntityToUserId(OrderEntity order) {
        return order.getOrderId();
    }

}