package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.criteria.OrderItemSearchCriteria;
import ee.taltech.iti03022024project.dto.OrderItemDto;
import ee.taltech.iti03022024project.responses.PageResponse;
import ee.taltech.iti03022024project.service.OrderItemService;
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
@RequestMapping("/api/order_items")
@Tag(name = "Order items", description = "Operations related to order item objects")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @Operation(summary = "Get all order items", description = "Returns a list of all order items recorded in the database.")
    @ApiResponse(responseCode = "200", description = "List of order items returned successfully.", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PageResponse.class))))
    @GetMapping
    public ResponseEntity<PageResponse<OrderItemDto>> getOrderItems(
            @Valid @ModelAttribute OrderItemSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        return ResponseEntity.ok(orderItemService.getOrderItems(criteria, pageNo, pageSize));
    }

    @Operation(summary = "Get order item by id", description = "Returns an order item with the specified id (non-negative integer).")
    @ApiResponse(responseCode = "200", description = "Order item returned successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderItemDto.class)))
    @ApiResponse(responseCode = "404", description = "Order item not found.", content = @Content())
    @GetMapping("/{id}")
    public OrderItemDto getOrderItemById(@PathVariable int id) {
        return orderItemService.getOrderItemById(id);
    }

    @Operation(summary = "Create order item", description = "Creates a new order item and returns it.")
    @ApiResponse(responseCode = "201", description = "Order item created successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderItemDto.class)))
    @PostMapping
    public ResponseEntity<OrderItemDto> createOrderItem(@Valid @RequestBody OrderItemDto orderItemDto) {
        log.info("Received request to create order: {}", orderItemDto);
        OrderItemDto createdOrderItem = orderItemService.createOrderItem(orderItemDto);
        log.info("OrderItem created successfully: {}", createdOrderItem);
        int id = createdOrderItem.getId();
        return ResponseEntity.created(URI.create(String.format("/api/order_items/%s", id))).body(createdOrderItem);
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
