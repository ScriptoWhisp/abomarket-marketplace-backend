package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.OrderDto;
import ee.taltech.iti03022024project.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable int id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
        log.info("Received request to create order: {}", orderDto);
        OrderDto createdOrder = orderService.createOrder(orderDto);
        log.info("Order created successfully: {}", createdOrder);
        return ResponseEntity.ok(createdOrder);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable int id, @RequestBody OrderDto orderDto) {
        log.info("Received request to update status of order with id {}, with data: {}", id, orderDto);
        OrderDto updatedOrder = orderService.updateOrder(id, orderDto);
        log.info("Order updated successfully: {}", updatedOrder);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable int id) {
        log.info("Received request to delete order with id {}", id);
        orderService.deleteOrder(id);
        log.info("Order deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }
}
