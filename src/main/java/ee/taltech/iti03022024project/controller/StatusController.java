package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.StatusDto;
import ee.taltech.iti03022024project.service.StatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @ApiResponse(responseCode = "200", description = "Status created successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StatusDto.class)))
    @PostMapping
    public ResponseEntity<StatusDto> createStatus(@RequestBody StatusDto statusDto) {
        return ResponseEntity.ok(statusService.createStatus(statusDto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update status", description = "Updates status with the specified id and returns it.")
    @ApiResponse(responseCode = "200", description = "Status updated successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StatusDto.class)))
    @ApiResponse(responseCode = "404", description = "Status not found.", content = @Content())
    public ResponseEntity<StatusDto> updateStatus(@PathVariable int id, @RequestBody StatusDto statusDto) {
        return ResponseEntity.ok(statusService.updateStatus(id, statusDto));
    }

    @Operation(summary = "Delete status", description = "Deletes status with the specified id.")
    @ApiResponse(responseCode = "204", description = "Status deleted successfully.")
    @ApiResponse(responseCode = "404", description = "Status not found.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable int id) {
        statusService.deleteStatus(id);
        return ResponseEntity.noContent().build();
    }
}
