package ee.taltech.iti03022024project.mapstruct;

import ee.taltech.iti03022024project.controller.OrderDto;
import ee.taltech.iti03022024project.repository.OrderEntity;
import ee.taltech.iti03022024project.repository.StatusEntity;
import ee.taltech.iti03022024project.repository.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "order_id", target = "id")
    @Mapping(source = "user.user_id", target = "userId")
    @Mapping(source = "status.status_id", target = "statusId")
    OrderDto toDto(OrderEntity orderEntity);

    @Mapping(source = "id", target = "order_id", ignore = true)
    @Mapping(source = "userId", target = "user")
    @Mapping(source = "statusId", target = "status")
    OrderEntity toEntity(OrderDto orderDto);

    default UserEntity mapUserIdToUserEntity(int userId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUser_id(userId);
        return userEntity;
    }

    default int mapUserEntityToUserId(UserEntity user) {
        return user.getUser_id();
    }

    default StatusEntity mapStatusIdToStatusEntity(int statusId) {
        StatusEntity statusEntity = new StatusEntity();
        statusEntity.setStatus_id(statusId);
        return statusEntity;
    }


    default int mapStatusEntityToStatusId(StatusEntity status) {
        return status.getStatus_id();
    }

}
