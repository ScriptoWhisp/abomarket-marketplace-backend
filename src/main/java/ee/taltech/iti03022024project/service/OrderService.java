package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.domain.OrderEntity;
import ee.taltech.iti03022024project.domain.StatusEntity;
import ee.taltech.iti03022024project.dto.OrderDto;
import ee.taltech.iti03022024project.exception.ObjectCreationException;
import ee.taltech.iti03022024project.exception.ResourceNotFoundException;
import ee.taltech.iti03022024project.mapstruct.OrderMapper;
import ee.taltech.iti03022024project.repository.OrderRepository;
import ee.taltech.iti03022024project.repository.StatusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final StatusRepository statusRepository;
    private final OrderMapper orderMapper;


    public List<OrderDto> getOrders() {
        return orderRepository.findAll().stream().map(orderMapper::toDto).toList();
    }

    public OrderDto getOrderById(int id) {
        return orderRepository.findById(id).map(orderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order with " + id + " not found"));
    }

    public OrderDto createOrder(OrderDto orderDto) {
        try {
            log.info("Attempting to create order with data: {}", orderDto);
            OrderEntity newOrder = orderMapper.toEntity(orderDto);
            OrderEntity savedOrder = orderRepository.save(newOrder);
            log.info("Order created successfully: {}", savedOrder);
            return orderMapper.toDto(savedOrder);
        } catch (Exception e) {
            throw new ObjectCreationException("Failed to create order: " + e.getMessage());
        }
    }

    public OrderDto updateOrder(int id, OrderDto orderDto) {
        log.info("Attempting to update order with id {}, with data: {}", id, orderDto);
        OrderEntity orderToUpdate = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
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
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
        orderRepository.delete(orderToDelete);
        log.info("Order deleted successfully: {}", orderToDelete);

    }
}
