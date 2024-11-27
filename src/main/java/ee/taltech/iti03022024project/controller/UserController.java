package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.UserDto;
import ee.taltech.iti03022024project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Operations related to user objects")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get all users", description = "Returns a list of all users recorded in the database.")
    @ApiResponse(responseCode = "200", description = "List of users returned successfully.", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDto.class))))
    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @Operation(summary = "Get user by id", description = "Returns a user with the specified id (non-negative integer).")
    @ApiResponse(responseCode = "200", description = "User returned successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @ApiResponse(responseCode = "404", description = "User not found.", content = @Content())
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable int id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Get authorized user", description = "Returns authorized user's userDTO (of the user who requested it).")
    @ApiResponse(responseCode = "200", description = "User returned successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getAuthorizedUser() {
        return ResponseEntity.ok(userService.getAuthorizedUser());
    }

    @Operation(summary = "Patch authorized user", description = "Patcher authorized user's information and returns updated info.")
    @ApiResponse(responseCode = "200", description = "User updated successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @PatchMapping("/profile")
    public ResponseEntity<UserDto> patchAuthorizedUser(@Valid @RequestBody UserDto userDto) {
        log.info("Received request to update authorized user with data: {}", userDto);
        UserDto updatedUser = userService.patchAuthorizedUser(userDto);
        log.info("Authorized user updated successfully: {}", updatedUser);
        return ResponseEntity.ok(updatedUser);
    }


    @Operation(summary = "Create user", description = "Creates a new user with the given information.")
    @ApiResponse(responseCode = "201", description = "User created successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @PostMapping

    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Received request to create user: {}", userDto);
        UserDto createdUser = userService.createUser(userDto);
        log.info("User created successfully: {}", createdUser);
        int userId = createdUser.getId();
        return ResponseEntity.created(URI.create(String.format("/api/users/%s", userId))).body(createdUser);
    }

    @Operation(summary = "Delete user", description = "Deletes a user with the specified id (non-negative integer).")
    @ApiResponse(responseCode = "204", description = "User deleted successfully.")
    @ApiResponse(responseCode = "404", description = "User not found.", content = @Content())
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@Valid @PathVariable int id) {
        log.info("Received request to delete user with id {}", id);
        userService.deleteUser(id);
        log.info("User deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }

}
