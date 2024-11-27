package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.StatusDto;
import ee.taltech.iti03022024project.service.StatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping("/api/statuses")
public class StatusController {

    private final StatusService statusService;


    @Operation(summary = "Get all statuses", description = "Returns a list of all statuses recorded in the database.")
    @ApiResponse(responseCode = "200", description = "List of statuses returned successfully.", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = StatusDto.class))))
    @GetMapping
    public List<StatusDto> getStatuses() {
        return statusService.getStatuses();
    }

    @Operation(summary = "Get status by id", description = "Returns a status with the specified id (non-negative integer).")
    @ApiResponse(responseCode = "200", description = "Status returned successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StatusDto.class)))
    @ApiResponse(responseCode = "404", description = "Status not found.", content = @Content())
    @GetMapping("/{id}")
    public ResponseEntity<StatusDto> getStatusById(@PathVariable int id) {
        return ResponseEntity.ok(statusService.getStatusById(id));
    }

    @Operation(summary = "Create status", description = "Creates a new status and returns it.")
    @ApiResponse(responseCode = "201", description = "Status created successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StatusDto.class)))
    @PostMapping
    public ResponseEntity<StatusDto> createStatus(@Valid @RequestBody StatusDto statusDto) {
        log.info("Received request to create status: {}", statusDto);
        StatusDto createdStatus = statusService.createStatus(statusDto);
        log.info("Status created successfully: {}", createdStatus);
        int id = createdStatus.getId();
        return ResponseEntity.created(URI.create(String.format("/api/statuses/%s", id))).body(createdStatus);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update status", description = "Updates status with the specified id and returns it.")
    @ApiResponse(responseCode = "200", description = "Status updated successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StatusDto.class)))
    @ApiResponse(responseCode = "404", description = "Status not found.", content = @Content())
    public ResponseEntity<StatusDto> updateStatus(@PathVariable int id, @Valid @RequestBody StatusDto statusDto) {
        log.info("Received request to update status with id {}, with data: {}", id,  statusDto);
        StatusDto updatedStatus = statusService.updateStatus(id, statusDto);
        log.info("Status updated successfully: {}", updatedStatus);
        return ResponseEntity.ok(updatedStatus);
    }

    @Operation(summary = "Delete status", description = "Deletes status with the specified id.")
    @ApiResponse(responseCode = "204", description = "Status deleted successfully.")
    @ApiResponse(responseCode = "404", description = "Status not found.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable int id) {
        log.info("Received request to delete status with id {}", id);
        statusService.deleteStatus(id);
        log.info("Status deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }
}
