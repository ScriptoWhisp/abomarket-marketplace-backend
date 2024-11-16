package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.domain.CategoryEntity;
import ee.taltech.iti03022024project.dto.CategoryDto;
import ee.taltech.iti03022024project.exception.ObjectCreationException;
import ee.taltech.iti03022024project.exception.ResourceNotFoundException;
import ee.taltech.iti03022024project.mapstruct.CategoryMapper;
import ee.taltech.iti03022024project.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getCategories() {
        return categoryRepository.findAll().stream().map(categoryMapper::toDto).toList();
    }

    public CategoryDto getCategoryById(int id) {
        return categoryRepository.findById(id).map(categoryMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found"));
    }

    public CategoryDto createCategory(CategoryDto categoryDto) {
        try {
            CategoryEntity newCategory = categoryMapper.toEntity(categoryDto);
            CategoryEntity savedCategory = categoryRepository.save(newCategory);
            return categoryMapper.toDto(savedCategory);
        } catch (Exception e) {
            throw new ObjectCreationException("Failed to create category: " + e.getMessage());
        }
    }

    public CategoryDto updateCategory(int id, CategoryDto categoryDto) {
        CategoryEntity categoryToUpdate = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found"));
        categoryToUpdate.setCategoryName(
                categoryDto.getName() != null ? categoryDto.getName() : categoryToUpdate.getCategoryName());

        CategoryEntity updatedCategory = categoryRepository.save(categoryToUpdate);

        return categoryMapper.toDto(updatedCategory);
    }

    public void deleteCategory(int id) {
        CategoryEntity categoryToDelete = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found"));

        categoryRepository.delete(categoryToDelete);
    }
}
