package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.criteria.OrderSearchCriteria;
import ee.taltech.iti03022024project.dto.OrderDto;
import ee.taltech.iti03022024project.responses.PageResponse;
import ee.taltech.iti03022024project.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Operations related to order objects")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Get all orders", description = "Returns a page of orders with the specified criteria, page number and page size.")
    @ApiResponse(responseCode = "200", description = "List of order items returned successfully.", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PageResponse.class))))
    @GetMapping
    public ResponseEntity<PageResponse<OrderDto>> getOrders(
            @Valid @ModelAttribute OrderSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        return ResponseEntity.ok(orderService.getOrders(criteria, pageNo, pageSize));
    }

    @Operation(summary = "Get order by id", description = "Returns an order with the specified id (non-negative integer).")
    @ApiResponse(responseCode = "200", description = "Order returned successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class)))
    @ApiResponse(responseCode = "404", description = "Order not found.", content = @Content())
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable int id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @Operation(summary = "Create order", description = "Creates a new order and returns it.")
    @ApiResponse(responseCode = "201", description = "Order created successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class)))
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderDto orderDto) {
        log.info("Received request to create order: {}", orderDto);
        OrderDto createdOrder = orderService.createOrder(orderDto);
        log.info("Order created successfully: {}", createdOrder);
        int id = createdOrder.getId();
        return ResponseEntity.created(URI.create(String.format("/api/orders/%s", id))).body(createdOrder);
    }

    @Operation(summary = "Update order", description = "Updates order with the specified id and returns it.")
    @ApiResponse(responseCode = "200", description = "Order updated successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class)))
    @ApiResponse(responseCode = "404", description = "Order not found.", content = @Content())
    @PatchMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable int id, @Valid @RequestBody OrderDto orderDto) {
        log.info("Received request to update status of order with id {}, with data: {}", id, orderDto);
        OrderDto updatedOrder = orderService.updateOrder(id, orderDto);
        log.info("Order updated successfully: {}", updatedOrder);
        return ResponseEntity.ok(updatedOrder);
    }

    @Operation(summary = "Delete order", description = "Deletes order with the specified id.")
    @ApiResponse(responseCode = "204", description = "Order deleted successfully.")
    @ApiResponse(responseCode = "404", description = "Order not found.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable int id) {
        log.info("Received request to delete order with id {}", id);
        orderService.deleteOrder(id);
        log.info("Order deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }
}
