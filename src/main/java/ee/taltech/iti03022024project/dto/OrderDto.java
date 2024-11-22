package ee.taltech.iti03022024project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;


@Schema(name = "Order", description = "DTO for information about order")
@Data
@AllArgsConstructor
public class OrderDto {

    @Schema(description = "Unique identifier of the order.", example = "1")
    @PositiveOrZero
    private Integer id;

    @Schema(description = "Unique identifier of the corresponding user (foreign key).", example = "1")
    @PositiveOrZero
    private Integer userId;

    @Schema(description = "Unique identifier of the corresponding status (such as \"packing\".", example = "1")
    @PositiveOrZero
    private Integer statusId;


}
