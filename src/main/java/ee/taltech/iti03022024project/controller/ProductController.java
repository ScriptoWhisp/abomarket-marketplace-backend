package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.ProductDto;
import ee.taltech.iti03022024project.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getProducts(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize) {
        return ResponseEntity.ok(productService.getProducts(pageNo, pageSize));
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
        return ResponseEntity.ok(productService.createProduct(productDto, token));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable int id, @RequestBody ProductDto productDto, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto, token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id, @RequestHeader("Authorization") String token) {
        productService.deleteProduct(id, token);
        return ResponseEntity.notFound().build();
    }

}
