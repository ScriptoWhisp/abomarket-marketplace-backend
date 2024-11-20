package ee.taltech.iti03022024project.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "statuses")
public class StatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private int statusId;
    private String statusName;
}
