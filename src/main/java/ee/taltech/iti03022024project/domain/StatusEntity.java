package ee.taltech.iti03022024project.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Schema(hidden = true)
@ToString
@Getter @Setter
@Entity(name = "statuses")
public class StatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private int statusId;
    private String statusName;
}
