package ee.taltech.iti03022024project.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(hidden = true)
@ToString
@Getter @Setter
@Entity(name = "roles")
public class RoleEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "role_id")
        private int roleId;

        @Column(name = "role_name")
        private String roleName;
}
