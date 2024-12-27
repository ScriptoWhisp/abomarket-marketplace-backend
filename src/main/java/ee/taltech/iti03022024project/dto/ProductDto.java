package ee.taltech.iti03022024project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;

@Schema(name = "Product", description = "DTO for information about product")
@Data
@Builder
@AllArgsConstructor
@Validated
public class ProductDto {

    @Schema(description = "Unique identifier of the product.", example = "1")
    private Integer id;

    @Schema(description = "Name of the product.", example = "Laptop")
    @NotEmpty
    @Size(max = 255)
    private String name;

    @Schema(description = "Description of the product.", example = "A laptop for work.")
    private String description;

    @Schema(description = "Price of the product.", example = "1000.0")
    @Positive
    private Double price;

    @Schema(description = "Quantity of the product in stock.", example = "10")
    @Positive
    private Integer stockQuantity;

    @Schema(description = "Unique identifier of the corresponding seller (foreign key).", example = "1")
    @Positive
    private Integer sellerId;

    @Schema(description = "Unique identifier of the corresponding category (foreign key).", example = "1")
    @NotNull
    private Integer categoryId;

    @Schema(description = "Date and time when the product was added to the database.", example = "2021-04-01T12:00:00Z")
    private Instant dateAdded;

    @Schema(description = "URL of the image of the product.", example = "https://example.com/image")
    private String imageUrl;

}
