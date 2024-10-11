package ee.taltech.iti03022024project.service;


import ee.taltech.iti03022024project.controller.StatusDto;
import ee.taltech.iti03022024project.mapstruct.StatusMapper;
import ee.taltech.iti03022024project.repository.StatusEntity;
import ee.taltech.iti03022024project.repository.StatusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;
    private final StatusMapper statusMapper;


    public List<StatusDto> getStatuses() {
        return statusRepository.findAll().stream().map(statusMapper::toDto).toList();
    }

    public Optional<StatusDto> getStatusById(int id) {
        return statusRepository.findById(id).map(statusMapper::toDto);
    }

    public Optional<StatusDto> createStatus(StatusDto statusDto) {
        StatusEntity newStatus = statusMapper.toEntity(statusDto);
        StatusEntity savedStatus = statusRepository.save(newStatus);
        return Optional.of(statusMapper.toDto(savedStatus));
    }

    public Optional<StatusDto> updateStatus(int id, StatusDto statusDto) {
        Optional<StatusEntity> statusToUpdate = statusRepository.findById(id);
        statusToUpdate.ifPresent(status -> {
            status.setStatus_name(statusDto.getName() != null ? statusDto.getName() : status.getStatus_name());
            statusRepository.save(status);
        });
        return statusToUpdate.map(statusMapper::toDto);
    }

    public Optional<StatusDto> deleteStatus(int id) {
        Optional<StatusEntity> statusToDelete = statusRepository.findById(id);
        statusToDelete.ifPresent(statusRepository::delete);
        return statusToDelete.map(statusMapper::toDto);
    }
}
