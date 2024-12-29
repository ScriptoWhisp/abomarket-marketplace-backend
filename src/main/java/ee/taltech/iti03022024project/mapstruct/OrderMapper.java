package ee.taltech.iti03022024project.mapstruct;

import ee.taltech.iti03022024project.domain.OrderEntity;
import ee.taltech.iti03022024project.domain.StatusEntity;
import ee.taltech.iti03022024project.domain.UserEntity;
import ee.taltech.iti03022024project.dto.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "orderId", target = "id")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "status.statusId", target = "statusId")
    OrderDto toDto(OrderEntity orderEntity);

    @Mapping(source = "id", target = "orderId", ignore = true)
    @Mapping(source = "userId", target = "user")
    @Mapping(source = "statusId", target = "status")
    OrderEntity toEntity(OrderDto orderDto);

    default UserEntity mapUserIdToUserEntity(int userId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userId);
        return userEntity;
    }

    default int mapUserEntityToUserId(UserEntity user) {
        return user.getUserId();
    }

    default StatusEntity mapStatusIdToStatusEntity(int statusId) {
        StatusEntity statusEntity = new StatusEntity();
        statusEntity.setStatusId(statusId);
        return statusEntity;
    }


    default int mapStatusEntityToStatusId(StatusEntity status) {
        return status.getStatusId();
    }

}
