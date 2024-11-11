package ee.taltech.iti03022024project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ProductDto {

    private Integer id;
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private Integer sellerId;
    private Integer categoryId;
    private Instant dateAdded;
    private String imageUrl;

}
