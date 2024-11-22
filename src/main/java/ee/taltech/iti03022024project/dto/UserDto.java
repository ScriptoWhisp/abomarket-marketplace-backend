package ee.taltech.iti03022024project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(name = "User", description = "DTO for information about user")
@Data
@AllArgsConstructor
public class UserDto {

    @Schema(description = "Unique identifier of the user.", example = "1")
    @PositiveOrZero
    private Integer id;

    @Schema(description = "First name of the user.", example = "John")
    @Size(max = 255)
    private String firstName;

    @Schema(description = "Last name of the user.", example = "Doe")
    @Size(max = 255)
    private String lastName;

    @Schema(description = "Email of the user.", example = "example@gmail.com")
    @Size(max = 255)
    private String email;

    @Schema(description = "Password of the user.", example = "password")
    @Size(max = 255)
    private String password;

    @Schema(description = "Phone number of the user.", example = "12345678")
    @Size(max = 255)
    private String phone;

    @Schema(description = "Address of the user.", example = "Tallinn")
    @Size(max = 255)
    private String location;

    @Schema(description = "Unique identifier of the unfinished order (foreign key).", example = "1")
    @PositiveOrZero
    private Integer unfinishedOrderId;

}
