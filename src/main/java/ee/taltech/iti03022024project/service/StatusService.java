package ee.taltech.iti03022024project.service;


import ee.taltech.iti03022024project.domain.StatusEntity;
import ee.taltech.iti03022024project.dto.StatusDto;
import ee.taltech.iti03022024project.exception.ResourceNotFoundException;
import ee.taltech.iti03022024project.mapstruct.StatusMapper;
import ee.taltech.iti03022024project.repository.StatusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;
    private final StatusMapper statusMapper;

    private static final String NOT_FOUND_MSG = "Status with id %s not found";


    public List<StatusDto> getStatuses() {
        return statusRepository.findAll().stream().map(statusMapper::toDto).toList();
    }

    public StatusDto getStatusById(int id) {
        return statusRepository.findById(id).map(statusMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG.formatted(id)));
    }

    public StatusDto createStatus(StatusDto statusDto) {
        try {
            log.info("Attempting to create status: {}", statusDto);
            StatusEntity newStatus = statusMapper.toEntity(statusDto);
            StatusEntity savedStatus = statusRepository.save(newStatus);
            log.info("Status created successfully: {}", savedStatus);
            return statusMapper.toDto(savedStatus);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to create status: " + e.getMessage());
        }
    }

    public StatusDto updateStatus(int id, StatusDto statusDto) {
        log.info("Attempting to update status with id {}, with data: {}", id, statusDto);
        StatusEntity statusToUpdate = statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG.formatted(id)));

        statusToUpdate.setStatusName(statusDto.getName() != null ? statusDto.getName() : statusToUpdate.getStatusName());

        StatusEntity updatedStatus = statusRepository.save(statusToUpdate);

        log.info("Status updated successfully: {}", updatedStatus);

        return statusMapper.toDto(updatedStatus);
    }

    public void deleteStatus(int id) {
        log.info("Attempting to delete status with id {}", id);
        StatusEntity statusToDelete = statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG.formatted(id)));
        statusRepository.delete(statusToDelete);
        log.info("Status deleted successfully: {}", statusToDelete);
    }
}
