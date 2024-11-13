package ee.taltech.iti03022024project.service;


import ee.taltech.iti03022024project.dto.ProductDto;
import ee.taltech.iti03022024project.mapstruct.ProductMapper;
import ee.taltech.iti03022024project.domain.CategoryEntity;
import ee.taltech.iti03022024project.repository.CategoryRepository;
import ee.taltech.iti03022024project.domain.ProductEntity;
import jakarta.transaction.Transactional;
import ee.taltech.iti03022024project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;


    public Page<ProductDto> findBooks(ProductCriteria criteria, int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        return productRepository.findAll(criteria, paging).map(productMapper::toDto);
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
