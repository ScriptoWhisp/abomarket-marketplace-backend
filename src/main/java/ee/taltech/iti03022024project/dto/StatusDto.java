package ee.taltech.iti03022024project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(name = "Status", description = "DTO for information about status")
@Data
@AllArgsConstructor
public class StatusDto {

    @Schema(description = "Unique identifier of the status.", example = "1")
    @PositiveOrZero
    private Integer id;

    @Schema(description = "Name of the status.", example = "Packing")
    @Size(max = 255)
    private String name;
}
