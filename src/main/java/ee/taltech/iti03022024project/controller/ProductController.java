package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.criteria.ProductSearchCriteria;
import ee.taltech.iti03022024project.dto.ProductDto;
import ee.taltech.iti03022024project.responses.PageResponse;
import ee.taltech.iti03022024project.security.JwtRequestFilter;
import ee.taltech.iti03022024project.service.ProductService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final JwtRequestFilter jwtRequestFilter;

    @GetMapping("/test")
    public ResponseEntity<PageResponse<ProductDto>> test(@Valid @ModelAttribute ProductSearchCriteria criteria) {
        return ResponseEntity.ok(productService.findBooks(criteria, 0, 5));
    }

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getProducts(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize) {
        return ResponseEntity.ok(productService.getProducts(pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable int id) {
        return productService.getProductById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Page<ProductDto>> getProductsByUserId(@PathVariable int id,
                                                                @RequestParam(defaultValue = "0") int pageNo,
                                                               @RequestParam(defaultValue = "5") int pageSize) {
        return ResponseEntity.ok(productService.getProductsByUserId(id, pageNo, pageSize));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto, @RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // parse token and check if userid is the same as in productDto
        // if not, return 403

        // because token is "Bearer ......"
        Claims claims = jwtRequestFilter.parseToken(token.split(" ")[1]);
        if (claims == null) {
            return ResponseEntity.badRequest().build();
        }

        int userId = (int) claims.get("userId");
        if (userId != productDto.getSellerId()) {
            return ResponseEntity.status(403).build();
        }

        return productService.createProduct(productDto).map(ResponseEntity::ok).orElse(ResponseEntity.internalServerError().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable int id, @RequestBody ProductDto productDto, @RequestHeader("Authorization") String token) {
        // reminder to myself: change forbidding so it checks the CURRENT product seller id as well


        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // parse token and check if userid is the same as in productDto
        // if not, return 403

        // because token is "Bearer ......"
        Claims claims = jwtRequestFilter.parseToken(token.split(" ")[1]);
        if (claims == null) {
            return ResponseEntity.badRequest().build();
        }

        int userId = (int) claims.get("userId");
        if (userId != productDto.getSellerId()) {
            return ResponseEntity.status(403).build();
        }

        return productService.updateProduct(id, productDto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id, @RequestHeader("Authorization") String token) {
        return productService.deleteProduct(id).isPresent() ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
