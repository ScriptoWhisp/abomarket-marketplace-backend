package ee.taltech.iti03022024project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(name = "Category", description = "DTO for information about category")
@Data
@AllArgsConstructor
public class CategoryDto {

    @Schema(description = "Unique identifier of the category.", example = "1")
    private Integer id;

    @Schema(description = "Name of the category.", example = "Electronics")
    private String name;
}
