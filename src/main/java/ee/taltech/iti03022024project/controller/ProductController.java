package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.criteria.ProductSearchCriteria;
import ee.taltech.iti03022024project.dto.ProductDto;
import ee.taltech.iti03022024project.responses.PageResponse;
import ee.taltech.iti03022024project.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Operations related to product objects")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get all products", description = "Returns a page with list of some products recorded in the database (depending on pagination parameters).")
    @ApiResponse(responseCode = "200", description = "List of products returned successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)))
    @GetMapping
    public ResponseEntity<PageResponse<ProductDto>> getProducts(
            @Valid @ModelAttribute ProductSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize) {
        return ResponseEntity.ok(productService.getProducts(criteria, pageNo, pageSize));
    }

    @Operation(summary = "Get product by id", description = "Returns a product with the specified id (non-negative integer).")
    @ApiResponse(responseCode = "200", description = "Product returned successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class)))
    @ApiResponse(responseCode = "404", description = "Product not found.", content = @Content())
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable int id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "Get products by user id", description = "Returns a page with list of some products recorded in the database (depending on pagination parameters) by user id.")
    @ApiResponse(responseCode = "200", description = "List of products returned successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)))
    @ApiResponse(responseCode = "500", description = "User not found.", content = @Content())
    @GetMapping("/user/{id}")
    public ResponseEntity<PageResponse<ProductDto>> getProductsByUserId(@PathVariable int id,
                                                                @RequestParam(defaultValue = "0") int pageNo,
                                                               @RequestParam(defaultValue = "5") int pageSize) {
        return ResponseEntity.ok(productService.getProductsByUserId(id, pageNo, pageSize));
    }

    @Operation(summary = "Create product", description = "Creates a new product and returns it.")
    @ApiResponse(responseCode = "201", description = "Product created successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class)))
    @ApiResponse(responseCode = "403", description = "Bad token.", content = @Content())
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto, @RequestHeader("Authorization") String token) {
        log.info("Received request to create product: {}", productDto);
        ProductDto createdProduct = productService.createProduct(productDto, token);
        log.info("Product created successfully: {}", createdProduct);
        int id = createdProduct.getId();
        return ResponseEntity.created(URI.create(String.format("/api/products/%s", id))).body(createdProduct);
    }

    @Operation(summary = "Update product", description = "Updates product with the specified id and returns it.")
    @ApiResponse(responseCode = "200", description = "Product updated successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class)))
    @ApiResponse(responseCode = "404", description = "Product not found.", content = @Content())
    @ApiResponse(responseCode = "403", description = "User's and seller's IDs do not match.", content = @Content())
    @PatchMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable int id, @Valid @RequestBody ProductDto productDto, @RequestHeader("Authorization") String token) {
        log.info("Received request to update product with id {}, with data: {}", id, productDto);
        ProductDto updatedProduct = productService.updateProduct(id, productDto, token);
        log.info("Product updated successfully: {}", updatedProduct);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Delete product", description = "Deletes product with the specified id.")
    @ApiResponse(responseCode = "204", description = "Product deleted successfully.")
    @ApiResponse(responseCode = "404", description = "Product not found.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id, @RequestHeader("Authorization") String token) {
        log.info("Received request to delete product with id {}", id);
        productService.deleteProduct(id, token);
        log.info("Product deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }

}
