package ee.taltech.iti03022024project.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(hidden = true)
@ToString
@Getter @Setter
@Entity(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int orderId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ToString.Exclude  // fixes infinite loop when trying to do userEntity.toString()
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "status_id")
    private StatusEntity status;
}
