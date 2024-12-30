package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.domain.CategoryEntity;
import ee.taltech.iti03022024project.dto.CategoryDto;
import ee.taltech.iti03022024project.exception.ObjectCreationException;
import ee.taltech.iti03022024project.exception.ResourceNotFoundException;
import ee.taltech.iti03022024project.mapstruct.CategoryMapper;
import ee.taltech.iti03022024project.repository.CategoryRepository;
import ee.taltech.iti03022024project.responses.PageResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    private static final String NOT_FOUND_MSG = "Category with id %s not found";

    public PageResponse<CategoryDto> getCategories(String search, int pageNo, int pageSize) {
        log.info("Attempting to get categories with search: {}, page number: {}, page size: {}", search, pageNo, pageSize);
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return new PageResponse<>(categoryRepository.findAllByCategoryNameContaining(search, pageable).map(categoryMapper::toDto));
    }

    public CategoryDto getCategoryById(int id) {
        return categoryRepository.findById(id).map(categoryMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG.formatted(id)));
    }

    public CategoryDto createCategory(CategoryDto categoryDto) {
        try {
            log.info("Attempting to create category with data: {}", categoryDto);
            CategoryEntity newCategory = categoryMapper.toEntity(categoryDto);
            CategoryEntity savedCategory = categoryRepository.save(newCategory);
            log.info("Category created successfully: {}", savedCategory);
            return categoryMapper.toDto(savedCategory);
        } catch (Exception e) {
            throw new ObjectCreationException("Failed to create category: " + e.getMessage());
        }
    }

    public CategoryDto updateCategory(int id, CategoryDto categoryDto) {
        log.info("Attempting to update category with id {}, with data: {}", id, categoryDto);
        CategoryEntity categoryToUpdate = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG.formatted(id)));
        categoryToUpdate.setCategoryName(
                categoryDto.getName() != null ? categoryDto.getName() : categoryToUpdate.getCategoryName());

        CategoryEntity updatedCategory = categoryRepository.save(categoryToUpdate);

        log.info("Category updated successfully: {}", updatedCategory);

        return categoryMapper.toDto(updatedCategory);
    }

    public void deleteCategory(int id) {
        log.info("Attempting to delete category with id {}", id);
        CategoryEntity categoryToDelete = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG.formatted(id)));
        categoryRepository.delete(categoryToDelete);
        log.info("Category deleted successfully: {}", categoryToDelete);
    }
}
