package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.criteria.ProductSearchCriteria;
import ee.taltech.iti03022024project.dto.ProductDto;
import ee.taltech.iti03022024project.responses.PageResponse;
import ee.taltech.iti03022024project.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PageResponse<ProductDto>> getProducts(
            @Valid @ModelAttribute ProductSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize) {
        return ResponseEntity.ok(productService.getProducts(criteria, pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable int id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Page<ProductDto>> getProductsByUserId(@PathVariable int id,
                                                                @RequestParam(defaultValue = "0") int pageNo,
                                                               @RequestParam(defaultValue = "5") int pageSize) {
        return ResponseEntity.ok(productService.getProductsByUserId(id, pageNo, pageSize));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto, @RequestHeader("Authorization") String token) {
        log.info("Received request to create product: {}", productDto);
        ProductDto createdProduct = productService.createProduct(productDto, token);
        log.info("Product created successfully: {}", createdProduct);
        return ResponseEntity.ok(createdProduct);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable int id, @RequestBody ProductDto productDto, @RequestHeader("Authorization") String token) {
        log.info("Received request to update product with id {}, with data: {}", id, productDto);
        ProductDto updatedProduct = productService.updateProduct(id, productDto, token);
        log.info("Product updated successfully: {}", updatedProduct);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id, @RequestHeader("Authorization") String token) {
        log.info("Received request to delete product with id {}", id);
        productService.deleteProduct(id, token);
        log.info("Product deleted successfully: {}", id);
        return ResponseEntity.notFound().build();
    }

}
