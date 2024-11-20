package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.CategoryDto;
import ee.taltech.iti03022024project.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories() {
        return categoryService.getCategories();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable int id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        log.info("Received request to create category: {}", categoryDto);
        CategoryDto createdCategory = categoryService.createCategory(categoryDto);
        log.info("Category created successfully: {}", createdCategory);
        return ResponseEntity.ok(createdCategory);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable int id, @RequestBody CategoryDto categoryDto) {
        log.info("Received request to update category with id {}, with data: {}", id, categoryDto);
        CategoryDto updatedCategory = categoryService.updateCategory(id, categoryDto);
        log.info("Category updated successfully: {}", categoryDto);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        log.info("Received request to delete category with id {}", id);
        categoryService.deleteCategory(id);
        log.info("Category deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }
}
