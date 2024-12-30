package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.CategoryDto;
import ee.taltech.iti03022024project.responses.PageResponse;
import ee.taltech.iti03022024project.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Operations related to category objects")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all categories", description = "Returns a list of all categories recorded in the database. Supports filtration and pagination.")
    @ApiResponse(responseCode = "200", description = "Page of categories returned successfully.", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PageResponse.class))))
    @GetMapping
    public ResponseEntity<PageResponse<CategoryDto>> getCategories(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        return ResponseEntity.ok(categoryService.getCategories(search, pageNo, pageSize));
    }

    @Operation(summary = "Get category by id", description = "Returns a category with the specified id (non-negative integer).")
    @ApiResponse(responseCode = "200", description = "Category returned successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDto.class)))
    @ApiResponse(responseCode = "404", description = "Category not found.", content = @Content())
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable int id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @Operation(summary = "Create category", description = "Creates a new category and returns it.")
    @ApiResponse(responseCode = "201", description = "Category created successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDto.class)))
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Received request to create category: {}", categoryDto);
        CategoryDto createdCategory = categoryService.createCategory(categoryDto);
        log.info("Category created successfully: {}", createdCategory);
        int id = createdCategory.getId();
        return ResponseEntity.created(URI.create(String.format("/api/categories/%s", id))).body(createdCategory);
    }

    @Operation(summary = "Update category", description = "Updates category with the specified id and returns it.")
    @ApiResponse(responseCode = "200", description = "Category updated successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDto.class)))
    @ApiResponse(responseCode = "404", description = "Category not found.", content = @Content())
    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable int id, @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Received request to update category with id {}, with data: {}", id, categoryDto);
        CategoryDto updatedCategory = categoryService.updateCategory(id, categoryDto);
        log.info("Category updated successfully: {}", categoryDto);
        return ResponseEntity.ok(updatedCategory);
    }

    @Operation(summary = "Delete category", description = "Deletes category with the specified id.")
    @ApiResponse(responseCode = "204", description = "Category deleted successfully.")
    @ApiResponse(responseCode = "404", description = "Category not found.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        log.info("Received request to delete category with id {}", id);
        categoryService.deleteCategory(id);
        log.info("Category deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }
}
