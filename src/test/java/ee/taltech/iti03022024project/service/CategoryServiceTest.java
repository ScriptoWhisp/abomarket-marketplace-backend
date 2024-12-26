package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.domain.CategoryEntity;
import ee.taltech.iti03022024project.dto.CategoryDto;
import ee.taltech.iti03022024project.exception.ObjectCreationException;
import ee.taltech.iti03022024project.exception.ResourceNotFoundException;
import ee.taltech.iti03022024project.mapstruct.CategoryMapper;
import ee.taltech.iti03022024project.repository.CategoryRepository;
import ee.taltech.iti03022024project.responses.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Captor
    private ArgumentCaptor<CategoryEntity> categoryEntityArgumentCaptor;

    private CategoryDto sampleCategoryDto;
    private CategoryEntity sampleCategoryEntity;

    @BeforeEach
    void setUp() {
        sampleCategoryDto = new CategoryDto(1, "SampleCategory");

        sampleCategoryEntity = new CategoryEntity();
        sampleCategoryEntity.setCategoryId(1);
        sampleCategoryEntity.setCategoryName("SampleCategory");
    }

    // ---------------------------------------------------------------------------------------------
    // getCategories
    // ---------------------------------------------------------------------------------------------
    @Test
    void getCategories_SearchTerm_ReturnsPageResponse() {
        // given
        String searchTerm = "Sample";
        int pageNo = 0;
        int pageSize = 5;

        Page<CategoryEntity> mockPage = new PageImpl<>(
                List.of(sampleCategoryEntity),
                PageRequest.of(pageNo, pageSize),
                1
        );

        // Mock repository and mapper
        when(categoryRepository.findAllByCategoryNameContaining(eq(searchTerm), any(PageRequest.class)))
                .thenReturn(mockPage);
        when(categoryMapper.toDto(sampleCategoryEntity)).thenReturn(sampleCategoryDto);

        // when
        PageResponse<CategoryDto> result = categoryService.getCategories(searchTerm, pageNo, pageSize);

        // then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals("SampleCategory", result.content().getFirst().getName());

        verify(categoryRepository, times(1))
                .findAllByCategoryNameContaining(eq(searchTerm), any(PageRequest.class));
        verify(categoryMapper, times(1)).toDto(sampleCategoryEntity);
    }

    @Test
    void getCategories_EmptySearchTerm_ReturnsEmptyPage() {
        // given
        String searchTerm = "";
        int pageNo = 0;
        int pageSize = 5;

        Page<CategoryEntity> emptyMockPage = new PageImpl<>(
                Collections.emptyList(),
                PageRequest.of(pageNo, pageSize),
                0
        );

        when(categoryRepository.findAllByCategoryNameContaining(eq(searchTerm), any(PageRequest.class)))
                .thenReturn(emptyMockPage);

        // when
        PageResponse<CategoryDto> result = categoryService.getCategories(searchTerm, pageNo, pageSize);

        // then
        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        verify(categoryRepository, times(1))
                .findAllByCategoryNameContaining(eq(searchTerm), any(PageRequest.class));
        verifyNoInteractions(categoryMapper);
    }

    // ---------------------------------------------------------------------------------------------
    // getCategoryById
    // ---------------------------------------------------------------------------------------------
    @Test
    void getCategoryById_ExistingId_ReturnsCategoryDto() {
        // given
        int categoryId = 1;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(sampleCategoryEntity));
        when(categoryMapper.toDto(sampleCategoryEntity)).thenReturn(sampleCategoryDto);

        // when
        CategoryDto result = categoryService.getCategoryById(categoryId);

        // then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("SampleCategory", result.getName());

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toDto(sampleCategoryEntity);
    }

    @Test
    void getCategoryById_NonExistingId_ThrowsResourceNotFoundException() {
        // given
        int categoryId = 999;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // when & then
        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.getCategoryById(categoryId)
        );
        assertTrue(thrown.getMessage().contains(String.valueOf(categoryId)));
        verify(categoryRepository, times(1)).findById(categoryId);
        verifyNoInteractions(categoryMapper);
    }

    // ---------------------------------------------------------------------------------------------
    // createCategory
    // ---------------------------------------------------------------------------------------------
    @Test
    void createCategory_ValidCategoryDto_ReturnsCreatedCategoryDto() {
        // given
        when(categoryMapper.toEntity(sampleCategoryDto)).thenReturn(sampleCategoryEntity);
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(sampleCategoryEntity);
        when(categoryMapper.toDto(sampleCategoryEntity)).thenReturn(sampleCategoryDto);

        // when
        CategoryDto result = categoryService.createCategory(sampleCategoryDto);

        // then
        assertNotNull(result);
        assertEquals("SampleCategory", result.getName());

        verify(categoryMapper, times(1)).toEntity(sampleCategoryDto);
        verify(categoryRepository, times(1)).save(any(CategoryEntity.class));
        verify(categoryMapper, times(1)).toDto(sampleCategoryEntity);
    }

    @Test
    void createCategory_RepositoryThrowsException_ThrowsObjectCreationException() {
        // given
        when(categoryMapper.toEntity(sampleCategoryDto)).thenReturn(sampleCategoryEntity);
        when(categoryRepository.save(sampleCategoryEntity)).thenThrow(new RuntimeException("DB error"));

        // when & then
        ObjectCreationException thrown = assertThrows(
                ObjectCreationException.class,
                () -> categoryService.createCategory(sampleCategoryDto)
        );

        assertTrue(thrown.getMessage().contains("Failed to create category"));
        verify(categoryMapper, times(1)).toEntity(sampleCategoryDto);
        verify(categoryRepository, times(1)).save(sampleCategoryEntity);
        verifyNoMoreInteractions(categoryMapper);
    }

    // ---------------------------------------------------------------------------------------------
    // updateCategory
    // ---------------------------------------------------------------------------------------------
    @Test
    void updateCategory_ExistingId_ReturnsUpdatedCategoryDto() {
        // given
        int categoryId = 1;
        CategoryDto updateDto = new CategoryDto(1, "UpdatedCategory");

        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setCategoryId(categoryId);
        existingCategory.setCategoryName("OldCategoryName");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(sampleCategoryEntity);
        when(categoryMapper.toDto(sampleCategoryEntity)).thenReturn(sampleCategoryDto);

        // when
        CategoryDto result = categoryService.updateCategory(categoryId, updateDto);

        // then
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).save(categoryEntityArgumentCaptor.capture());
        verify(categoryMapper, times(1)).toDto(sampleCategoryEntity);

        CategoryEntity savedEntity = categoryEntityArgumentCaptor.getValue();
        assertEquals("UpdatedCategory", savedEntity.getCategoryName());
        assertEquals(sampleCategoryDto, result);
    }

    @Test
    void updateCategory_NonExistingId_ThrowsResourceNotFoundException() {
        // given
        int categoryId = 999;
        CategoryDto updateDto = new CategoryDto(999, "UpdatedCategory");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // when & then
        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.updateCategory(categoryId, updateDto)
        );
        assertTrue(thrown.getMessage().contains(String.valueOf(categoryId)));
        verify(categoryRepository, times(1)).findById(categoryId);
        verifyNoInteractions(categoryMapper);
    }

    @Test
    void updateCategory_NameIsNull_SetsOldCategoryName() {
        // given
        int categoryId = 1;
        CategoryDto updateDto = new CategoryDto(1, null); // Name is null
        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setCategoryId(categoryId);
        existingCategory.setCategoryName("OldName");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(existingCategory);
        when(categoryMapper.toDto(existingCategory)).thenReturn(new CategoryDto(1, "OldName"));

        // when
        CategoryDto result = categoryService.updateCategory(categoryId, updateDto);

        // then
        assertEquals("OldName", result.getName()); // Assert old name is used
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).save(existingCategory);
    }


    // ---------------------------------------------------------------------------------------------
    // deleteCategory
    // ---------------------------------------------------------------------------------------------
    @Test
    void deleteCategory_ExistingId_DeletesCategory() {
        // given
        int categoryId = 1;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(sampleCategoryEntity));

        // when
        categoryService.deleteCategory(categoryId);

        // then
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).delete(sampleCategoryEntity);
    }

    @Test
    void deleteCategory_NonExistingId_ThrowsResourceNotFoundException() {
        // given
        int categoryId = 999;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // when & then
        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(categoryId)
        );
        assertTrue(thrown.getMessage().contains(String.valueOf(categoryId)));
        verify(categoryRepository, times(1)).findById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }
}
