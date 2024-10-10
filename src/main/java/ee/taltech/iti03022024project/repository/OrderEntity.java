package ee.taltech.iti03022024project.repository;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int order_id;
    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "user_id")
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name="status_id", referencedColumnName = "status_id")
    private StatusEntity status;
}
