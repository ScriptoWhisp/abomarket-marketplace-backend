package ee.taltech.iti03022024project.repository;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int order_item_id;
    @ManyToOne
    @JoinColumn(name="order_id", referencedColumnName = "order_id")
    private OrderEntity order;
    @ManyToOne
    @JoinColumn(name="product_id", referencedColumnName = "product_id")
    private ProductEntity product;
    private int quantity;
    private double price_at_time_of_order;
}
