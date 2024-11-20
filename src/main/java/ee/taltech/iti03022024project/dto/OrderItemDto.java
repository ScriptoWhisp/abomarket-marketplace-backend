package ee.taltech.iti03022024project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(name = "OrderItem", description = "DTO for information about order item")
@Data
@AllArgsConstructor
public class OrderItemDto {

    @Schema(description = "Unique identifier of the order item object.", example = "1")
    private Integer id;

    @Schema(description = "Unique identifier of the corresponding order (foreign key).", example = "1")
    private Integer orderId;

    @Schema(description = "Unique identifier of the corresponding product (foreign key).", example = "1")
    private Integer productId;

    @Schema(description = "Quantity of the product in the order.", example = "2")
    private Integer quantity;

    @Schema(description = "Price of the product at the time of order.", example = "10.0")
    private Double priceAtTimeOfOrder;
}
