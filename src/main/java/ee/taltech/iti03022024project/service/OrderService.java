package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.criteria.OrderSearchCriteria;
import ee.taltech.iti03022024project.domain.OrderEntity;
import ee.taltech.iti03022024project.domain.StatusEntity;
import ee.taltech.iti03022024project.domain.UserEntity;
import ee.taltech.iti03022024project.dto.OrderDto;
import ee.taltech.iti03022024project.exception.ObjectCreationException;
import ee.taltech.iti03022024project.exception.ResourceNotFoundException;
import ee.taltech.iti03022024project.mapstruct.OrderMapper;
import ee.taltech.iti03022024project.repository.OrderRepository;
import ee.taltech.iti03022024project.repository.StatusRepository;
import ee.taltech.iti03022024project.responses.PageResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final StatusRepository statusRepository;
    private final OrderMapper orderMapper;

    private static final String NOT_FOUND_MSG = "Order with id %s not found";


    public PageResponse<OrderDto> getOrders(OrderSearchCriteria criteria, int pageNo, int pageSize) {

        Specification<OrderEntity> spec = Specification.where(null);

        if (criteria.id() != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), criteria.id()));
        }

        if (criteria.userId() != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("userId"), criteria.userId()));
        }

        if (criteria.statusId() != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status").get("statusId"), criteria.statusId()));
        }

        if (pageNo < 0) {
            pageNo = 0;
        }

        if (pageSize < 1) {
            pageSize = 1;
        }

        Pageable paging = PageRequest.of(pageNo, pageSize);

        Page<OrderEntity> page = orderRepository.findAll(spec, paging);
        return new PageResponse<>(page.map(orderMapper::toDto));

    }

    public OrderDto getOrderById(int id) {
        return orderRepository.findById(id).map(orderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG.formatted(id)));
    }

    public OrderDto createOrder(OrderDto orderDto) {
        try {
            log.info("Attempting to create order with data: {}", orderDto);
            OrderEntity newOrder = orderMapper.toEntity(orderDto);

            // fetch status from db to avoid detached entity exception
            StatusEntity status = statusRepository.findById(orderDto.getStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Status with id " + orderDto.getStatusId() + " not found"));
            newOrder.setStatus(status);

            OrderEntity savedOrder = orderRepository.save(newOrder);
            log.info("Order created successfully: {}", savedOrder);
            return orderMapper.toDto(savedOrder);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ObjectCreationException("Failed to create order: " + e.getMessage());
        }
    }

    // This method returns OrderEntity as it must be only used in UserService, thus not facing controller
    public OrderEntity createUnfinishedOrderForUser(UserEntity userEntity) {
        try {
            log.info("Attempting to create cart for user {}", userEntity);
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setUser(userEntity);
            orderEntity.setStatus(statusRepository.getReferenceById(0));  // cart status has id of 0
            log.info("Trying to save cart {}", orderEntity);
            return orderRepository.save(orderEntity);
        } catch (Exception e) {
            throw new ObjectCreationException("Failed to create cart: " + e.getMessage());
        }


    }

    public OrderDto updateOrder(int id, OrderDto orderDto) {
        log.info("Attempting to update order with id {}, with data: {}", id, orderDto);
        OrderEntity orderToUpdate = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG.formatted(id)));
        StatusEntity newStatus = statusRepository.findById(orderDto.getStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Status with id " + orderDto.getStatusId() + " not found"));

        orderToUpdate.setStatus(newStatus);

        orderRepository.save(orderToUpdate);

        log.info("Order updated successfully: {}", orderToUpdate);

        return orderMapper.toDto(orderToUpdate);
    }

    public void deleteOrder(int id) {
        log.info("Attempting to delete order with id {}", id);
        OrderEntity orderToDelete = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG.formatted(id)));
        orderRepository.delete(orderToDelete);
        log.info("Order deleted successfully: {}", orderToDelete);

    }
}
