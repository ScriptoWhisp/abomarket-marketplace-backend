package ee.taltech.iti03022024project.service;


import ee.taltech.iti03022024project.controller.ProductDto;
import ee.taltech.iti03022024project.mapstruct.ProductMapper;
import ee.taltech.iti03022024project.repository.ProductRepository;
import ee.taltech.iti03022024project.repository.ProductEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductDto> getProducts() {
        return productRepository.findAll().stream().map(productMapper::toDto).toList();
    }

    public Optional<ProductDto> getProductById(int id) {
        return productRepository.findById(id).map(productMapper::toDto);
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
            product.setQuantity_in_stock(productDto.getStockQuantity() != null ? productDto.getStockQuantity() : product.getQuantity_in_stock());
            // skipping setting seller because why would you need to do that
            // also it's a bit harder to implement
            product.setCategory_id(productDto.getCategoryId() != null ? productDto.getCategoryId() : product.getCategory_id());
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
