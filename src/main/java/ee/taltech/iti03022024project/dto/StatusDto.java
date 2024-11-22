package ee.taltech.iti03022024project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(name = "Status", description = "DTO for information about status")
@Data
@AllArgsConstructor
public class StatusDto {

    @Schema(description = "Unique identifier of the status.", example = "1")
    private Integer id;

    @Schema(description = "Name of the status.", example = "Packing")
    private String name;
}
