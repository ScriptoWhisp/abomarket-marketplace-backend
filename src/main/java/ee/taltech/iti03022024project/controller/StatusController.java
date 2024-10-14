package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.StatusDto;
import ee.taltech.iti03022024project.service.StatusService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return statusService.getStatusById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StatusDto> createStatus(@RequestBody StatusDto statusDto) {
        return statusService.createStatus(statusDto).map(ResponseEntity::ok).orElse(ResponseEntity.internalServerError().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<StatusDto> updateStatus(@PathVariable int id, @RequestBody StatusDto statusDto) {
        return statusService.updateStatus(id, statusDto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable int id) {
        return statusService.deleteStatus(id).isPresent() ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
