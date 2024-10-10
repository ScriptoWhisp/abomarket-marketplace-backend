package ee.taltech.iti03022024project.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

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
    private OffsetDateTime dateAdded;

}
