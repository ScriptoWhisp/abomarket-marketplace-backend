package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.OrderItemDto;
import ee.taltech.iti03022024project.service.OrderItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/order_items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping
    public List<OrderItemDto> getOrderItems() {
        return orderItemService.getOrderItems();
    }

    @GetMapping("/{id}")
    public Optional<OrderItemDto> getOrderItemById(@PathVariable int id) {
        return orderItemService.getOrderItemById(id);
    }

    @PostMapping
    public ResponseEntity<OrderItemDto> createOrderItem(@RequestBody OrderItemDto orderItemDto) {
        return orderItemService.createOrderItem(orderItemDto).map(ResponseEntity::ok).orElse(ResponseEntity.internalServerError().build());
    }

    // No need to implement updateOrderItem method for now

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable int id) {
        return orderItemService.deleteOrderItem(id).isPresent() ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
