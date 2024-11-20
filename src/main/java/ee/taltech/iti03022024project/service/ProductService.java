package ee.taltech.iti03022024project.service;


import ee.taltech.iti03022024project.criteria.ProductSearchCriteria;
import ee.taltech.iti03022024project.domain.CategoryEntity;
import ee.taltech.iti03022024project.domain.ProductEntity;
import ee.taltech.iti03022024project.dto.ProductDto;
import ee.taltech.iti03022024project.exception.BadTokenException;
import ee.taltech.iti03022024project.exception.ObjectCreationException;
import ee.taltech.iti03022024project.exception.ResourceNotFoundException;
import ee.taltech.iti03022024project.mapstruct.ProductMapper;
import ee.taltech.iti03022024project.repository.CategoryRepository;
import ee.taltech.iti03022024project.repository.ProductRepository;
import ee.taltech.iti03022024project.repository.UsersRepository;
import ee.taltech.iti03022024project.responses.PageResponse;
import ee.taltech.iti03022024project.security.JwtRequestFilter;
import ee.taltech.iti03022024project.specifications.ProductSpecifications;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final JwtRequestFilter jwtRequestFilter;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final UsersRepository usersRepository;

    public PageResponse<ProductDto> getProducts(ProductSearchCriteria criteria, int pageNo, int pageSize) {
        // criteria
        Specification<ProductEntity> spec = Specification.where(null);

        if (criteria.productId() != null) {
            spec = spec.and(ProductSpecifications.hasId(criteria.productId()));
        }

        if (criteria.name() != null) {
            spec = spec.and(ProductSpecifications.hasName(criteria.name()));
        }

        if (criteria.description() != null) {
            spec = spec.and(ProductSpecifications.hasSubstringInDescription(criteria.description()));
        }

        if (criteria.price() != null) {
            spec = spec.and(ProductSpecifications.priceInRange(criteria.price(), criteria.price()));
        }

        if (criteria.quantityInStock() != null) {
            spec = spec.and(ProductSpecifications.leftAtLeast(criteria.quantityInStock()));
        }

        if (criteria.seller() != null) {
            spec = spec.and(ProductSpecifications.hasSeller(criteria.seller().getUserId()));
        }

        if (criteria.category() != null) {
            spec = spec.and(ProductSpecifications.hasCategory(criteria.category().getCategoryId()));
        }

//        if (criteria.dateAddedMin() != null && criteria.dateAddedMax() != null) {
//            spec = spec.and(ProductSpecifications.inDateRange(criteria.dateAddedMin(), criteria.dateAddedMax()));
//        }

        String sortBy = criteria.sortDirection() == null ? "ASC" : criteria.sortDirection();
        Sort sort = Sort.by(Sort.Direction.valueOf(sortBy), "productId");
        Pageable paging = PageRequest.of(pageNo, pageSize, sort);

        Page<ProductEntity> page = productRepository.findAll(spec, paging);
        PageResponse<ProductDto> response = new PageResponse<>(page.map(productMapper::toDto));

        return response;
    }

    private void tokenValidation(ProductDto productDto, String token) {
        log.info("Starting token validation");
        if (token == null || token.isEmpty()) {
            throw new BadTokenException("Token is null or empty");
        }

        Claims claims = jwtRequestFilter.parseToken(token.split(" ")[1]);
        if (claims == null) {
            throw new BadTokenException("Invalid token");
        }

        int userId = (int) claims.get("userId");
        if (userId != productDto.getSellerId()) {
            throw new BadTokenException("User and seller id not match");
        }
        log.info("Token validation completed successfully");
    }

    public ProductDto getProductById(int id) {
        return productRepository.findById(id).map(productMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
    }

    public Page<ProductDto> getProductsByUserId(int id, int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        if (usersRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        return productRepository.findAllBySeller_UserId(id, paging).map(productMapper::toDto);
    }

    public ProductDto createProduct(ProductDto productDto, String token) {
        tokenValidation(productDto, token);

        try {
            log.info("Attempting to create product with data: {}", productDto);
            ProductEntity newProduct = productMapper.toEntity(productDto);
            ProductEntity savedProduct = productRepository.save(newProduct);
            log.info("Product created successfully: {}", savedProduct);
            return productMapper.toDto(savedProduct);
        } catch (Exception e) {
            throw new ObjectCreationException("Failed to create product: " + e.getMessage());
        }
    }

    public ProductDto updateProduct(int id, ProductDto productDto, String token) {
        tokenValidation(productDto, token);

        log.info("Attempting to update product with id {}, with data: {}", id, productDto);

        ProductEntity productToUpdate = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));

        if (productDto.getName() != null) {
            productToUpdate.setName(productDto.getName());
        }
        if (productDto.getDescription() != null) {
            productToUpdate.setDescription(productDto.getDescription());
        }
        if (productDto.getPrice() != null) {
            productToUpdate.setPrice(productDto.getPrice());
        }
        if (productDto.getStockQuantity() != null) {
            productToUpdate.setQuantityInStock(productDto.getStockQuantity());
        }
        // skipping setting seller because why would you need to do that
        // also it's a bit harder to implement

        if (productDto.getCategoryId() != null) {
            CategoryEntity newCategory = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category with id " + productDto.getCategoryId() + " not found"));
            productToUpdate.setCategory(newCategory);
        }

        if (productDto.getImageUrl() != null) {
            productToUpdate.setImageUrl(productDto.getImageUrl());
        }

        productRepository.save(productToUpdate);

        log.info("Product updated successfully: {}", productToUpdate);

        return productMapper.toDto(productToUpdate);
    }

    public void deleteProduct(int id, String token) {
        log.info("Attempting to delete product with id {}", id);
        ProductEntity productToDelete = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));

        tokenValidation(productMapper.toDto(productToDelete), token);

        productRepository.delete(productToDelete);

    }

}
