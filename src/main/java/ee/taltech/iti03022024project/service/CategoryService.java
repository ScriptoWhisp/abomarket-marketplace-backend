package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.controller.CategoryDto;
import ee.taltech.iti03022024project.mapstruct.CategoryMapper;
import ee.taltech.iti03022024project.repository.CategoryEntity;
import ee.taltech.iti03022024project.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getCategories() {
        return categoryRepository.findAll().stream().map(categoryMapper::toDto).toList();
    }

    public Optional<CategoryDto> getCategoryById(int id) {
        return categoryRepository.findById(id).map(categoryMapper::toDto);
    }

    public Optional<CategoryDto> createCategory(CategoryDto categoryDto) {
        CategoryEntity newCategory = categoryMapper.toEntity(categoryDto);
        CategoryEntity savedCategory = categoryRepository.save(newCategory);
        return Optional.of(categoryMapper.toDto(savedCategory));
    }

    public Optional<CategoryDto> updateCategory(int id, CategoryDto categoryDto) {
        Optional<CategoryEntity> categoryToUpdate = categoryRepository.findById(id);
        categoryToUpdate.ifPresent(category -> {
            category.setCategory_name(categoryDto.getName() != null ? categoryDto.getName() : category.getCategory_name());
            categoryRepository.save(category);
        });
        return categoryToUpdate.map(categoryMapper::toDto);
    }

    public Optional<CategoryDto> deleteCategory(int id) {
        Optional<CategoryEntity> categoryToDelete = categoryRepository.findById(id);
        categoryToDelete.ifPresent(categoryRepository::delete);
        return categoryToDelete.map(categoryMapper::toDto);
    }
}
