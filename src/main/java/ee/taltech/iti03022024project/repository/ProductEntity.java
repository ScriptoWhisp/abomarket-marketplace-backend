package ee.taltech.iti03022024project.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Data
@Entity(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int productId;
    private String name;
    private String description;
    private double price;
    private int quantityInStock;
    @ManyToOne
    @JoinColumn(name="seller_id", referencedColumnName = "user_id")
    private UserEntity seller;

    @ManyToOne
    @JoinColumn(name="category_id", referencedColumnName = "category_id")
    private CategoryEntity category;

    // still unsure whether to use LDT or ODT
    @CreationTimestamp
    private OffsetDateTime dateAdded;


}
