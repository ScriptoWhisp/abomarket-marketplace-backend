package ee.taltech.iti03022024project.service;


import ee.taltech.iti03022024project.criteria.ProductSearchCriteria;
import ee.taltech.iti03022024project.dto.ProductDto;
import ee.taltech.iti03022024project.mapstruct.ProductMapper;
import ee.taltech.iti03022024project.domain.CategoryEntity;
import ee.taltech.iti03022024project.repository.CategoryRepository;
import ee.taltech.iti03022024project.domain.ProductEntity;
import ee.taltech.iti03022024project.responses.PageResponse;
import ee.taltech.iti03022024project.specifications.ProductSpecifications;
import jakarta.transaction.Transactional;
import ee.taltech.iti03022024project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public PageResponse<ProductDto> findBooks(ProductSearchCriteria criteria, int pageNo, int pageSize) {
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

    public Page<ProductDto> getProducts(int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        return productRepository.findAll(paging).map(productMapper::toDto);
    }

    public Optional<ProductDto> getProductById(int id) {
        return productRepository.findById(id).map(productMapper::toDto);
    }

    public Page<ProductDto> getProductsByUserId(int id, int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        return productRepository.findAllBySeller_UserId(id, paging).map(productMapper::toDto);
    }

    public Optional<ProductDto> createProduct(ProductDto productDto) {
        ProductEntity newProduct = productMapper.toEntity(productDto);
        ProductEntity savedProduct = productRepository.save(newProduct);
        return Optional.of(productMapper.toDto(savedProduct));
    }

    public Optional<ProductDto> updateProduct(int id, ProductDto productDto) {
        Optional<ProductEntity> productToUpdate = productRepository.findById(id);
        productToUpdate.ifPresent(product -> {
            product.setName(productDto.getName() != null ? productDto.getName() : product.getName());
            product.setDescription(productDto.getDescription() != null ? productDto.getDescription() : product.getDescription());
            product.setPrice(productDto.getPrice() != null ? productDto.getPrice() : product.getPrice());
            product.setQuantityInStock(productDto.getStockQuantity() != null ? productDto.getStockQuantity() : product.getQuantityInStock());
            // skipping setting seller because why would you need to do that
            // also it's a bit harder to implement

            Optional<CategoryEntity> newCategoryOpt = categoryRepository.findById(productDto.getCategoryId());
            if (newCategoryOpt.isPresent()) {
                product.setCategory(productDto.getCategoryId() != null ? newCategoryOpt.get() : product.getCategory());
            }

            product.setImageUrl(productDto.getImageUrl() != null ? productDto.getImageUrl() : product.getImageUrl());

            productRepository.save(product);
        });
        return productToUpdate.map(productMapper::toDto);
    }

    public Optional<ProductDto> deleteProduct(int id) {
        Optional<ProductEntity> productToDelete = productRepository.findById(id);
        productToDelete.ifPresent(productRepository::delete);
        return productToDelete.map(productMapper::toDto);
    }

}
