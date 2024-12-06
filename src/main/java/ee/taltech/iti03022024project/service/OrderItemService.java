package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.criteria.OrderItemSearchCriteria;
import ee.taltech.iti03022024project.domain.OrderItemEntity;
import ee.taltech.iti03022024project.dto.OrderItemDto;
import ee.taltech.iti03022024project.exception.ObjectCreationException;
import ee.taltech.iti03022024project.exception.ResourceNotFoundException;
import ee.taltech.iti03022024project.mapstruct.OrderItemMapper;
import ee.taltech.iti03022024project.repository.OrderItemRepository;
import ee.taltech.iti03022024project.responses.PageResponse;
import ee.taltech.iti03022024project.specifications.OrderItemsSpecifications;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;


    public PageResponse<OrderItemDto> getOrderItems(OrderItemSearchCriteria criteria, int pageNo, int pageSize) {
        // criteria
        Specification<OrderItemEntity> spec = Specification.where(null);


        if (criteria.id() != null) {
            spec = spec.and(OrderItemsSpecifications.hasId(criteria.id()));
        }

        if (criteria.quantity() != null) {
            spec = spec.and(OrderItemsSpecifications.hasQuantity(criteria.quantity()));
        }

        if (criteria.productId() != null) {
            spec = spec.and(OrderItemsSpecifications.hasProductId(criteria.productId()));
        }

        if (criteria.orderId() != null) {
            spec = spec.and(OrderItemsSpecifications.hasOrderId(criteria.orderId()));
        }

        if (criteria.priceAtTimeOfOrder() != null) {
            spec = spec.and(OrderItemsSpecifications.hasPriceAtTimeOfOrder(criteria.priceAtTimeOfOrder()));
        }

        if (pageNo < 0) {
            pageNo = 0;
        }

        if (pageSize < 1) {
            pageSize = 1;
        }


        Pageable paging = PageRequest.of(pageNo, pageSize);

        Page<OrderItemEntity> page = orderItemRepository.findAll(spec, paging);
        return new PageResponse<>(page.map(orderItemMapper::toDto));
    }

    public OrderItemDto getOrderItemById(int id) {
        return orderItemRepository.findById(id).map(orderItemMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order item with id " + id + " not found"));
    }

    public OrderItemDto createOrderItem(OrderItemDto orderItemDto) {
        try {
            log.info("Attempting to create order item with data: {}", orderItemDto);
            OrderItemEntity newOrderItem = orderItemMapper.toEntity(orderItemDto);
            OrderItemEntity savedOrderItem = orderItemRepository.save(newOrderItem);
            log.info("Order item created successfully: {}", savedOrderItem);
            return orderItemMapper.toDto(savedOrderItem);
        } catch (Exception e) {
            throw new ObjectCreationException("Failed to create order item: " + e.getMessage());
        }
    }


    public void deleteOrderItem(int id) {
        log.info("Attempting to delete order item with id {}", id);
        OrderItemEntity orderItemToDelete = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order item with id " + id + " not found"));
        orderItemRepository.delete(orderItemToDelete);
        log.info("Order item deleted successfully: {}", orderItemToDelete);

    }
}

