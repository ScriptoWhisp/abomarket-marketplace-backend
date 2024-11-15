package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.StatusDto;
import ee.taltech.iti03022024project.service.StatusService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.ok(statusService.createStatus(statusDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StatusDto> updateStatus(@PathVariable int id, @RequestBody StatusDto statusDto) {
        return ResponseEntity.ok(statusService.updateStatus(id, statusDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable int id) {
        statusService.deleteStatus(id);
        return ResponseEntity.noContent().build();
    }
}
