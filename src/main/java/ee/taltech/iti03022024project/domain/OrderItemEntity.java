package ee.taltech.iti03022024project.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(hidden = true)
@ToString
@Getter @Setter
@Entity(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderItemId;
    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private OrderEntity order;
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private ProductEntity product;
    private int quantity;
    private double priceAtTimeOfOrder;
}
