package ee.taltech.iti03022024project.repository;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Data
@Entity(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int product_id;
    private String name;
    private String description;
    private double price;
    private int quantity_in_stock;
    @ManyToOne
    @JoinColumn(name="seller_id", referencedColumnName = "user_id")
    private UserEntity seller;
    // change it to be a category later, just id is good enough for now
    private int category_id;

    // still unsure whether to use LDT or ODT
    @CreationTimestamp
    private OffsetDateTime date_added;


}
