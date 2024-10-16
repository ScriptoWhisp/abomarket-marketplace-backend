package ee.taltech.iti03022024project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemDto {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private Integer quantity;
    private Double priceAtTimeOfOrder;
}
