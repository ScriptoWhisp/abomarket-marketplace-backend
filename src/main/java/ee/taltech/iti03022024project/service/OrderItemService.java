package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.domain.OrderItemEntity;
import ee.taltech.iti03022024project.dto.OrderItemDto;
import ee.taltech.iti03022024project.exception.ObjectCreationException;
import ee.taltech.iti03022024project.exception.ResourceNotFoundException;
import ee.taltech.iti03022024project.mapstruct.OrderItemMapper;
import ee.taltech.iti03022024project.repository.OrderItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    public List<OrderItemDto> getOrderItems() {
        return orderItemRepository.findAll().stream().map(orderItemMapper::toDto).toList();
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

