package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.StatusDto;
import ee.taltech.iti03022024project.service.StatusService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/statuses")
public class StatusController {

    private final StatusService statusService;

    @GetMapping
    public List<StatusDto> getStatuses() {
        return statusService.getStatuses();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StatusDto> getStatusById(@PathVariable int id) {
        return ResponseEntity.ok(statusService.getStatusById(id));
    }

    @PostMapping
    public ResponseEntity<StatusDto> createStatus(@RequestBody StatusDto statusDto) {
        log.info("Received request to create status: {}", statusDto);
        StatusDto createdStatus = statusService.createStatus(statusDto);
        log.info("Status created successfully: {}", createdStatus);
        return ResponseEntity.ok(createdStatus);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StatusDto> updateStatus(@PathVariable int id, @RequestBody StatusDto statusDto) {
        log.info("Received request to update status with id {}, with data: {}", id,  statusDto);
        StatusDto updatedStatus = statusService.updateStatus(id, statusDto);
        log.info("Status updated successfully: {}", updatedStatus);
        return ResponseEntity.ok(updatedStatus);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable int id) {
        log.info("Received request to delete status with id {}", id);
        statusService.deleteStatus(id);
        log.info("Status deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }
}
