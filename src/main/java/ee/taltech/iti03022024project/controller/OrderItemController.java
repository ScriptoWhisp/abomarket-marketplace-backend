package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.OrderItemDto;
import ee.taltech.iti03022024project.service.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/order_items")
@Tag(name = "Order items", description = "Operations related to order item objects")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @Operation(summary = "Get all order items", description = "Returns a list of all order items recorded in the database.")
    @ApiResponse(responseCode = "200", description = "List of order items returned successfully.", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = OrderItemDto.class))))
    @GetMapping
    public List<OrderItemDto> getOrderItems() {
        return orderItemService.getOrderItems();
    }

    @Operation(summary = "Get order item by id", description = "Returns an order item with the specified id (non-negative integer).")
    @ApiResponse(responseCode = "200", description = "Order item returned successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderItemDto.class)))
    @ApiResponse(responseCode = "404", description = "Order item not found.", content = @Content())
    @GetMapping("/{id}")
    public OrderItemDto getOrderItemById(@PathVariable int id) {
        return orderItemService.getOrderItemById(id);
    }

    @Operation(summary = "Create order item", description = "Creates a new order item and returns it.")
    @ApiResponse(responseCode = "200", description = "Order item created successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderItemDto.class)))
    @PostMapping
    public ResponseEntity<OrderItemDto> createOrderItem(@RequestBody OrderItemDto orderItemDto) {
        log.info("Received request to create order: {}", orderItemDto);
        OrderItemDto createdOrderItem = orderItemService.createOrderItem(orderItemDto);
        log.info("OrderItem created successfully: {}", createdOrderItem);
        return ResponseEntity.ok(createdOrderItem);
    }

    // No need to implement updateOrderItem method for now

    @Operation(summary = "Delete order item", description = "Deletes order item with the specified id.")
    @ApiResponse(responseCode = "204", description = "Order item deleted successfully.")
    @ApiResponse(responseCode = "404", description = "Order item not found.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable int id) {
        log.info("Received request to delete order item with id {}", id);
        orderItemService.deleteOrderItem(id);
        log.info("OrderItem deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }
}
