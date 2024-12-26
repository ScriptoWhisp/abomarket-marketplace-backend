package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.domain.StatusEntity;
import ee.taltech.iti03022024project.dto.StatusDto;
import ee.taltech.iti03022024project.exception.ResourceNotFoundException;
import ee.taltech.iti03022024project.mapstruct.StatusMapper;
import ee.taltech.iti03022024project.repository.StatusRepository;
import ee.taltech.iti03022024project.responses.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusServiceTest {

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private StatusMapper statusMapper;

    @InjectMocks
    private StatusService statusService;

    private StatusDto sampleStatusDto;
    private StatusEntity sampleStatusEntity;

    @BeforeEach
    void setUp() {
        sampleStatusDto = new StatusDto(1, "SampleStatus");

        sampleStatusEntity = new StatusEntity();
        sampleStatusEntity.setStatusId(1);
        sampleStatusEntity.setStatusName("SampleStatus");
    }

    // ---------------------------------------------------------------------------------------------
    // getStatuses
    // ---------------------------------------------------------------------------------------------
    @Test
    void getStatuses_SearchTerm_ReturnsPageResponse() {
        // given
        String searchTerm = "Sample";
        int pageNo = 0;
        int pageSize = 5;

        Page<StatusEntity> mockPage = new PageImpl<>(
                List.of(sampleStatusEntity),
                PageRequest.of(pageNo, pageSize),
                1
        );

        when(statusRepository.findAllByStatusNameContaining(eq(searchTerm), any(PageRequest.class)))
                .thenReturn(mockPage);
        when(statusMapper.toDto(sampleStatusEntity)).thenReturn(sampleStatusDto);

        // when
        PageResponse<StatusDto> result = statusService.getStatuses(searchTerm, pageNo, pageSize);

        // then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals("SampleStatus", result.content().getFirst().getName());

        verify(statusRepository, times(1))
                .findAllByStatusNameContaining(eq(searchTerm), any(PageRequest.class));
        verify(statusMapper, times(1)).toDto(sampleStatusEntity);
    }

    @Test
    void getStatuses_EmptySearchTerm_ReturnsEmptyPage() {
        // given
        String searchTerm = "";
        int pageNo = 0;
        int pageSize = 5;

        Page<StatusEntity> emptyMockPage = new PageImpl<>(
                Collections.emptyList(),
                PageRequest.of(pageNo, pageSize),
                0
        );

        when(statusRepository.findAllByStatusNameContaining(eq(searchTerm), any(PageRequest.class)))
                .thenReturn(emptyMockPage);

        // when
        PageResponse<StatusDto> result = statusService.getStatuses(searchTerm, pageNo, pageSize);

        // then
        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        verify(statusRepository, times(1))
                .findAllByStatusNameContaining(eq(searchTerm), any(PageRequest.class));
        verifyNoInteractions(statusMapper);
    }

    // ---------------------------------------------------------------------------------------------
    // getStatusById
    // ---------------------------------------------------------------------------------------------
    @Test
    void getStatusById_ExistingId_ReturnsStatusDto() {
        // given
        int statusId = 1;
        when(statusRepository.findById(statusId)).thenReturn(Optional.of(sampleStatusEntity));
        when(statusMapper.toDto(sampleStatusEntity)).thenReturn(sampleStatusDto);

        // when
        StatusDto result = statusService.getStatusById(statusId);

        // then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("SampleStatus", result.getName());

        verify(statusRepository, times(1)).findById(statusId);
        verify(statusMapper, times(1)).toDto(sampleStatusEntity);
    }

    @Test
    void getStatusById_NonExistingId_ThrowsResourceNotFoundException() {
        // given
        int statusId = 999;
        when(statusRepository.findById(statusId)).thenReturn(Optional.empty());

        // when & then
        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class,
                () -> statusService.getStatusById(statusId)
        );

        assertTrue(thrown.getMessage().contains(String.valueOf(statusId)));
        verify(statusRepository, times(1)).findById(statusId);
        verifyNoInteractions(statusMapper);
    }

    // ---------------------------------------------------------------------------------------------
    // createStatus
    // ---------------------------------------------------------------------------------------------
    @Test
    void createStatus_ValidStatusDto_ReturnsCreatedStatusDto() {
        // given
        when(statusMapper.toEntity(sampleStatusDto)).thenReturn(sampleStatusEntity);
        when(statusRepository.save(any(StatusEntity.class))).thenReturn(sampleStatusEntity);
        when(statusMapper.toDto(sampleStatusEntity)).thenReturn(sampleStatusDto);

        // when
        StatusDto result = statusService.createStatus(sampleStatusDto);

        // then
        assertNotNull(result);
        assertEquals("SampleStatus", result.getName());

        verify(statusMapper, times(1)).toEntity(sampleStatusDto);
        verify(statusRepository, times(1)).save(any(StatusEntity.class));
        verify(statusMapper, times(1)).toDto(sampleStatusEntity);
    }

    @Test
    void createStatus_RepositoryThrowsException_ThrowsResourceNotFoundException() {
        // given
        when(statusMapper.toEntity(sampleStatusDto)).thenReturn(sampleStatusEntity);
        when(statusRepository.save(any(StatusEntity.class))).thenThrow(new RuntimeException("DB error"));

        // when & then
        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class,
                () -> statusService.createStatus(sampleStatusDto)
        );

        // then
        assertTrue(thrown.getMessage().contains("Failed to create status"));
        verify(statusMapper, times(1)).toEntity(sampleStatusDto);
        verify(statusRepository, times(1)).save(sampleStatusEntity);
        // No further interactions with statusMapper after the save throws
        verifyNoMoreInteractions(statusMapper);
    }

    // ---------------------------------------------------------------------------------------------
    // updateStatus
    // ---------------------------------------------------------------------------------------------
    @Test
    void updateStatus_ExistingId_ReturnsUpdatedStatusDto() {
        // given
        int statusId = 1;
        StatusDto updateDto = new StatusDto(1, "UpdatedStatus");

        StatusEntity existingStatus = new StatusEntity();
        existingStatus.setStatusId(statusId);
        existingStatus.setStatusName("OldStatus");

        when(statusRepository.findById(statusId)).thenReturn(Optional.of(existingStatus));
        when(statusRepository.save(existingStatus)).thenReturn(sampleStatusEntity);
        when(statusMapper.toDto(sampleStatusEntity)).thenReturn(sampleStatusDto);

        // when
        StatusDto result = statusService.updateStatus(statusId, updateDto);

        // then
        // The sampleStatusDto returned from the mapper has name = "SampleStatus"
        assertEquals("SampleStatus", result.getName());
        verify(statusRepository, times(1)).findById(statusId);
        verify(statusRepository, times(1)).save(existingStatus);
        verify(statusMapper, times(1)).toDto(sampleStatusEntity);
    }

    @Test
    void updateStatus_NameIsNull_UsesExistingStatusName() {
        // given
        int statusId = 1;
        StatusDto updateDto = new StatusDto(1, null); // name is null

        StatusEntity existingStatus = new StatusEntity();
        existingStatus.setStatusId(statusId);
        existingStatus.setStatusName("OldStatus");

        // Mock the repository call
        when(statusRepository.findById(statusId)).thenReturn(Optional.of(existingStatus));

        // Mock saving
        when(statusRepository.save(existingStatus)).thenReturn(existingStatus);

        // Mock mapper
        when(statusMapper.toDto(existingStatus)).thenReturn(new StatusDto(1, "OldStatus"));

        // when
        StatusDto result = statusService.updateStatus(statusId, updateDto);

        // then
        assertNotNull(result);
        assertEquals("OldStatus", result.getName());
        verify(statusRepository, times(1)).findById(statusId);
        // The name should remain "OldStatus"
        verify(statusRepository, times(1)).save(existingStatus);
        verify(statusMapper, times(1)).toDto(existingStatus);
    }

    @Test
    void updateStatus_NonExistingId_ThrowsResourceNotFoundException() {
        // given
        int statusId = 999;
        StatusDto updateDto = new StatusDto(999, "UpdatedStatus");

        when(statusRepository.findById(statusId)).thenReturn(Optional.empty());

        // when & then
        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class,
                () -> statusService.updateStatus(statusId, updateDto)
        );

        assertTrue(thrown.getMessage().contains(String.valueOf(statusId)));
        verify(statusRepository, times(1)).findById(statusId);
        verifyNoInteractions(statusMapper);
    }

    // ---------------------------------------------------------------------------------------------
    // deleteStatus
    // ---------------------------------------------------------------------------------------------
    @Test
    void deleteStatus_ExistingId_DeletesStatus() {
        // given
        int statusId = 1;
        when(statusRepository.findById(statusId)).thenReturn(Optional.of(sampleStatusEntity));

        // when
        statusService.deleteStatus(statusId);

        // then
        verify(statusRepository, times(1)).findById(statusId);
        verify(statusRepository, times(1)).delete(sampleStatusEntity);
    }

    @Test
    void deleteStatus_NonExistingId_ThrowsResourceNotFoundException() {
        // given
        int statusId = 999;
        when(statusRepository.findById(statusId)).thenReturn(Optional.empty());

        // when & then
        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class,
                () -> statusService.deleteStatus(statusId)
        );

        assertTrue(thrown.getMessage().contains(String.valueOf(statusId)));
        verify(statusRepository, times(1)).findById(statusId);
        verifyNoMoreInteractions(statusRepository);
    }
}
