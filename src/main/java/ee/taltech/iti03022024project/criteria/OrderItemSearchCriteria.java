package ee.taltech.iti03022024project.criteria;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;


@Builder
public record OrderItemSearchCriteria(
        @PositiveOrZero
        Integer id,
        @PositiveOrZero
        Integer orderId,
        @PositiveOrZero
        Integer productId,
        @Positive
        Integer quantity,
        @PositiveOrZero
        Double priceAtTimeOfOrder
) {


}
