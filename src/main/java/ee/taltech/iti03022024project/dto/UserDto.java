package ee.taltech.iti03022024project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(name = "User", description = "DTO for information about user")
@Data
@AllArgsConstructor
public class UserDto {

    @Schema(description = "Unique identifier of the user.", example = "1")
    private Integer id;

    @Schema(description = "First name of the user.", example = "John")
    private String firstName;

    @Schema(description = "Last name of the user.", example = "Doe")
    private String lastName;

    @Schema(description = "Email of the user.", example = "example@gmail.com")
    private String email;

    @Schema(description = "Password of the user.", example = "password")
    private String password;

    @Schema(description = "Phone number of the user.", example = "12345678")
    private String phone;

    @Schema(description = "Address of the user.", example = "Tallinn")
    private String location;

    @Schema(description = "Unique identifier of the unfinished order (foreign key).", example = "1")
    private Integer unfinishedOrderId;

}
