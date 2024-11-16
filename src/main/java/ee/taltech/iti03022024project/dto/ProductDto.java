package ee.taltech.iti03022024project.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;

@Data
@AllArgsConstructor
@Validated
public class ProductDto {

    private Integer id;
    @NotEmpty
    private String name;
    private String description;
    @Positive
    private Double price;
    @Positive
    private Integer stockQuantity;
    @Positive
    private Integer sellerId;
    @NotNull
    private Integer categoryId;
    private Instant dateAdded;
    private String imageUrl;

}
