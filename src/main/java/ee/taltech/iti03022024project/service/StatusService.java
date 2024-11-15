package ee.taltech.iti03022024project.service;


import ee.taltech.iti03022024project.domain.StatusEntity;
import ee.taltech.iti03022024project.dto.StatusDto;
import ee.taltech.iti03022024project.exception.ResourceNotFoundException;
import ee.taltech.iti03022024project.mapstruct.StatusMapper;
import ee.taltech.iti03022024project.repository.StatusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;
    private final StatusMapper statusMapper;


    public List<StatusDto> getStatuses() {
        return statusRepository.findAll().stream().map(statusMapper::toDto).toList();
    }

    public StatusDto getStatusById(int id) {
        return statusRepository.findById(id).map(statusMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Status with id " + id + " not found"));
    }

    public StatusDto createStatus(StatusDto statusDto) {
        try {
            StatusEntity newStatus = statusMapper.toEntity(statusDto);
            StatusEntity savedStatus = statusRepository.save(newStatus);
            return statusMapper.toDto(savedStatus);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to create status: " + e.getMessage());
        }
    }

    public StatusDto updateStatus(int id, StatusDto statusDto) {
        StatusEntity statusToUpdate = statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status with id " + id + " not found"));

        statusToUpdate.setStatusName(statusDto.getName() != null ? statusDto.getName() : statusToUpdate.getStatusName());

        StatusEntity updatedStatus = statusRepository.save(statusToUpdate);

        return statusMapper.toDto(updatedStatus);
    }

    public void deleteStatus(int id) {
        StatusEntity statusToDelete = statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status with id " + id + " not found"));
        statusRepository.delete(statusToDelete);
    }
}
