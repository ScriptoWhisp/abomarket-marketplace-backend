package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.CategoryDto;
import ee.taltech.iti03022024project.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Operations related to category objects")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all categories", description = "Returns a list of all categories recorded in the database.")
    @ApiResponse(responseCode = "200", description = "List of categories returned successfully.", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))))
    @GetMapping
    public List<CategoryDto> getCategories() {
        return categoryService.getCategories();
    }

    @Operation(summary = "Get category by id", description = "Returns a category with the specified id (non-negative integer).")
    @ApiResponse(responseCode = "200", description = "Category returned successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDto.class)))
    @ApiResponse(responseCode = "404", description = "Category not found.", content = @Content())
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable int id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @Operation(summary = "Create category", description = "Creates a new category and returns it.")
    @ApiResponse(responseCode = "200", description = "Category created successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDto.class)))
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.createCategory(categoryDto));
    }

    @Operation(summary = "Update category", description = "Updates category with the specified id and returns it.")
    @ApiResponse(responseCode = "200", description = "Category updated successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDto.class)))
    @ApiResponse(responseCode = "404", description = "Category not found.", content = @Content())
    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable int id, @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDto));
    }

    @Operation(summary = "Delete category", description = "Deletes category with the specified id.")
    @ApiResponse(responseCode = "204", description = "Category deleted successfully.")
    @ApiResponse(responseCode = "404", description = "Category not found.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
