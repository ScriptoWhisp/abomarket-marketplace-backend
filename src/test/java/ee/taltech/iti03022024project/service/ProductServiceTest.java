package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.criteria.ProductSearchCriteria;
import ee.taltech.iti03022024project.domain.CategoryEntity;
import ee.taltech.iti03022024project.domain.ProductEntity;
import ee.taltech.iti03022024project.domain.UserEntity;
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
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private JwtRequestFilter jwtRequestFilter;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private ProductService productService;

    private ProductDto sampleProductDto;
    private ProductEntity sampleProductEntity;
    private final String validToken = "Bearer validTokenString";
    private final Claims validClaims = mock(Claims.class);

    @BeforeEach
    void setUp() {
        // Prepare a sample ProductDto
        sampleProductDto = ProductDto.builder()
                .id(1)
                .name("Sample Product")
                .description("Sample Desc")
                .price(10.0)
                .stockQuantity(5)
                .sellerId(123)
                .categoryId(11)
                .dateAdded(Instant.now())
                .imageUrl("http://example.com/image.jpg")
                .build();

        // Prepare a sample ProductEntity
        sampleProductEntity = new ProductEntity();
        sampleProductEntity.setProductId(1);
        sampleProductEntity.setName("Sample Product");
        sampleProductEntity.setDescription("Sample Desc");
        sampleProductEntity.setPrice(10.0);
        sampleProductEntity.setQuantityInStock(5);
        sampleProductEntity.setImageUrl("http://example.com/image.jpg");
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(123);
        sampleProductEntity.setSeller(userEntity);

        // Set up valid token claims: userId = 123
        when(validClaims.get("userId")).thenReturn(123);

        // By default, parseToken(...) returns validClaims
        lenient().when(jwtRequestFilter.parseToken("validTokenString")).thenReturn(validClaims);
        lenient().when(validClaims.get("roles")).thenReturn(new ArrayList<>(List.of(new LinkedHashMap<>(Map.of("authority", "ROLE_USER")))));
    }

    // ---------------------------------------------------------------------------------------------
    // tokenValidation coverage (indirectly tested via create/update/delete), plus direct scenarios
    // ---------------------------------------------------------------------------------------------
    @Test
    void createProduct_TokenIsNull_ThrowsBadTokenException() {
        // given
        String nullToken = null;

        // when & then
        BadTokenException thrown = assertThrows(
                BadTokenException.class,
                () -> productService.createProduct(sampleProductDto, nullToken)
        );

        assertTrue(thrown.getMessage().contains("Token is null or empty"));
        verifyNoInteractions(jwtRequestFilter); // no parse attempt
    }

    @Test
    void createProduct_TokenIsEmpty_ThrowsBadTokenException() {
        // given
        String emptyToken = "";

        // when & then
        BadTokenException thrown = assertThrows(
                BadTokenException.class,
                () -> productService.createProduct(sampleProductDto, emptyToken)
        );

        assertTrue(thrown.getMessage().contains("Token is null or empty"));
        verifyNoInteractions(jwtRequestFilter); // no parse attempt
    }

    @Test
    void createProduct_ParseTokenReturnsNullClaims_ThrowsBadTokenException() {
        // given
        when(jwtRequestFilter.parseToken("validTokenString")).thenReturn(null);

        // when & then
        BadTokenException thrown = assertThrows(
                BadTokenException.class,
                () -> productService.createProduct(sampleProductDto, validToken)
        );

        assertTrue(thrown.getMessage().contains("Invalid token"));
        verify(jwtRequestFilter).parseToken("validTokenString");
    }

    @Test
    void createProduct_UserIdMismatch_ThrowsBadTokenException() {
        // given
        when(validClaims.get("userId")).thenReturn(999); // mismatch
        when(jwtRequestFilter.parseToken("validTokenString")).thenReturn(validClaims);

        // when & then
        BadTokenException thrown = assertThrows(
                BadTokenException.class,
                () -> productService.createProduct(sampleProductDto, validToken)
        );
        assertTrue(thrown.getMessage().contains("User and seller id not match"));
    }
    // ---------------------------------------------------------------------------------------------
    // getProducts
    // ---------------------------------------------------------------------------------------------
    @Test
    void getProducts_ValidCriteria_ReturnsPageResponse() {
        // given
        ProductSearchCriteria criteria = ProductSearchCriteria.builder()
                .name("Sample Product")
                .sortDirection("ASC")
                .sortBy("name")
                .build();

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "name"));
        Page<ProductEntity> pageResult = new PageImpl<>(List.of(sampleProductEntity), pageable, 1);

        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageResult);
        when(productMapper.toDto(sampleProductEntity)).thenReturn(sampleProductDto);

        // when
        PageResponse<ProductDto> response = productService.getProducts(criteria, 0, 5);

        // then
        assertNotNull(response);
        assertEquals(1, response.content().size());
        assertEquals("Sample Product", response.content().getFirst().getName());
        verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(productMapper, times(1)).toDto(sampleProductEntity);
    }

    @Test
    void getProducts_PageNoBelowZero_ResetsToZero() {
        // given
        ProductSearchCriteria criteria = ProductSearchCriteria.builder().build();
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "productId"));

        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

        // when
        PageResponse<ProductDto> page = productService.getProducts(criteria, -1, 5);

        // then
        assertNotNull(page);
        verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_PageSizeBelowOne_ResetsToOne() {
        // given
        ProductSearchCriteria criteria = new ProductSearchCriteria(null, null, null, null, null, null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "productId"));

        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

        // when
        PageResponse<ProductDto> page = productService.getProducts(criteria, 0, 0);

        // then
        assertNotNull(page);
        verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_AllCriteriaNull_ReturnsAll() {
        // given
        ProductSearchCriteria criteria = ProductSearchCriteria.builder().build();

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "productId"));
        Page<ProductEntity> pageResult = new PageImpl<>(List.of(sampleProductEntity), pageable, 1);

        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageResult);
        when(productMapper.toDto(sampleProductEntity)).thenReturn(sampleProductDto);

        // when
        PageResponse<ProductDto> result = productService.getProducts(criteria, 0, 5);

        // then
        assertFalse(result.content().isEmpty());
        verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getProducts_AllCriteriaNonNull_AllSpecsApplied() {
        // given
        UserEntity critSeller = new UserEntity();
        critSeller.setUserId(999);

        CategoryEntity critCategory = new CategoryEntity();
        critCategory.setCategoryId(888);

        ProductSearchCriteria criteria = ProductSearchCriteria.builder()
                .productId(1)
                .name("Sample Product")
                .description("Sample Desc")
                .price(10.0)
                .quantityInStock(5)
                .seller(critSeller)
                .category(critCategory)
                .sortDirection("DESC")
                .sortBy("price")
                .imageUrl("http://example.com/image.jpg")
                .build();

        Page<ProductEntity> pageResult = new PageImpl<>(
                List.of(sampleProductEntity),
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "price")),
                1
        );

        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(pageResult);
        when(productMapper.toDto(sampleProductEntity))
                .thenReturn(sampleProductDto);

        // when
        PageResponse<ProductDto> response = productService.getProducts(criteria, 0, 5);

        // then
        assertEquals(1, response.content().size());
        assertEquals("Sample Product", response.content().getFirst().getName());
        verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    // ---------------------------------------------------------------------------------------------
    // getProductById
    // ---------------------------------------------------------------------------------------------
    @Test
    void getProductById_ExistingId_ReturnsProductDto() {
        // given
        int productId = 1;
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProductEntity));
        when(productMapper.toDto(sampleProductEntity)).thenReturn(sampleProductDto);

        // when
        ProductDto result = productService.getProductById(productId);

        // then
        assertNotNull(result);
        assertEquals(productId, result.getId());
        verify(productRepository, times(1)).findById(productId);
        verify(productMapper, times(1)).toDto(sampleProductEntity);
    }

    @Test
    void getProductById_NonExistingId_ThrowsResourceNotFoundException() {
        // given
        int productId = 999;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProductById(productId)
        );
        assertTrue(thrown.getMessage().contains("Product with id 999 not found"));
    }

    // ---------------------------------------------------------------------------------------------
    // getProductsByUserId
    // ---------------------------------------------------------------------------------------------
    @Test
    void getProductsByUserId_UserExists_ReturnsPageResponse() {
        // given
        int userId = 123;
        when(usersRepository.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        Page<ProductEntity> pageResult = new PageImpl<>(List.of(sampleProductEntity), PageRequest.of(0,5), 1);

        when(productRepository.findAllBySeller_UserId(userId, PageRequest.of(0,5))).thenReturn(pageResult);
        when(productMapper.toDto(sampleProductEntity)).thenReturn(sampleProductDto);

        // when
        PageResponse<ProductDto> response = productService.getProductsByUserId(userId, 0, 5);

        // then
        assertNotNull(response);
        assertEquals(1, response.content().size());
        assertEquals("Sample Product", response.content().getFirst().getName());
    }

    @Test
    void getProductsByUserId_UserNotFound_ThrowsResourceNotFoundException() {
        // given
        int userId = 999;
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProductsByUserId(userId, 0, 5)
        );
        assertTrue(thrown.getMessage().contains("User with id 999 not found"));
    }

    // ---------------------------------------------------------------------------------------------
    // createProduct
    // ---------------------------------------------------------------------------------------------
    @Test
    void createProduct_ValidTokenAndDto_ReturnsCreatedProduct() {
        // given
        when(productMapper.toEntity(sampleProductDto)).thenReturn(sampleProductEntity);
        when(productRepository.save(sampleProductEntity)).thenReturn(sampleProductEntity);
        when(productMapper.toDto(sampleProductEntity)).thenReturn(sampleProductDto);

        // when
        ProductDto result = productService.createProduct(sampleProductDto, validToken);

        // then
        assertNotNull(result);
        assertEquals(sampleProductDto, result);
        verify(productRepository).save(sampleProductEntity);
    }

    @Test
    void createProduct_RepositoryThrowsException_ThrowsObjectCreationException() {
        // given
        when(productMapper.toEntity(sampleProductDto)).thenReturn(sampleProductEntity);
        when(productRepository.save(sampleProductEntity)).thenThrow(new RuntimeException("DB error"));

        // when & then
        ObjectCreationException thrown = assertThrows(
                ObjectCreationException.class,
                () -> productService.createProduct(sampleProductDto, validToken)
        );
        assertTrue(thrown.getMessage().contains("Failed to create product"));
        verify(productRepository).save(sampleProductEntity);
    }

    @Test
    void createProduct_AdminCreates_CreateProduct() {
        // given
        when(productMapper.toEntity(sampleProductDto)).thenReturn(sampleProductEntity);
        when(productRepository.save(sampleProductEntity)).thenReturn(sampleProductEntity);
        when(productMapper.toDto(sampleProductEntity)).thenReturn(sampleProductDto);
        when(validClaims.get("userId")).thenReturn(999);
        when(validClaims.get("roles")).thenReturn(new ArrayList<>(List.of(new LinkedHashMap<>(Map.of("authority", "ROLE_ADMIN")))));

        // when
        ProductDto result = productService.createProduct(sampleProductDto, validToken);

        // then
        assertNotNull(result);
        assertEquals(sampleProductDto, result);
        verify(productRepository).save(sampleProductEntity);
    }

    // ---------------------------------------------------------------------------------------------
    // updateProduct
    // ---------------------------------------------------------------------------------------------
    @Test
    void updateProduct_ExistingIdAndValidToken_UpdatesAndReturnsProductDto() {
        // given
        int productId = 1;
        ProductDto updateDto = new ProductDto(
                1,
                "Updated Product",
                "Updated Desc",
                99.99,
                50,
                123, // must match token userId
                999, // categoryId
                Instant.now(),
                "http://example.com/new.jpg"
        );

        CategoryEntity newCategory = new CategoryEntity();
        newCategory.setCategoryId(999);

        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProductEntity));
        when(categoryRepository.findById(999)).thenReturn(Optional.of(newCategory));

        when(productMapper.toDto(sampleProductEntity)).thenReturn(updateDto);

        // when
        ProductDto result = productService.updateProduct(productId, updateDto, validToken);

        // then
        assertNotNull(result);
        assertEquals("Updated Product", sampleProductEntity.getName());
        assertEquals(99.99, sampleProductEntity.getPrice());
        assertEquals("Updated Desc", sampleProductEntity.getDescription());
        assertEquals(50, sampleProductEntity.getQuantityInStock());
        assertEquals(999, sampleProductEntity.getCategory().getCategoryId());
        assertEquals("http://example.com/new.jpg", sampleProductEntity.getImageUrl());

        verify(productRepository).save(sampleProductEntity);
        assertEquals(updateDto, result);
    }

    @Test
    void updateProduct_AdminUpdatesProduct_ReturnsProductDto() {
        // given
        int productId = 1;
        ProductDto updateDto = new ProductDto(
                1,
                "Updated Product",
                "Updated Desc",
                99.99,
                50,
                123, // must match token userId
                999, // categoryId
                Instant.now(),
                "http://example.com/new.jpg"
        );

        CategoryEntity newCategory = new CategoryEntity();
        newCategory.setCategoryId(999);

        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProductEntity));
        when(categoryRepository.findById(999)).thenReturn(Optional.of(newCategory));
        when(validClaims.get("userId")).thenReturn(999);
        when(validClaims.get("roles")).thenReturn(new ArrayList<>(List.of(new LinkedHashMap<>(Map.of("authority", "ROLE_ADMIN")))));

        when(productMapper.toDto(sampleProductEntity)).thenReturn(updateDto);

        // when
        ProductDto result = productService.updateProduct(productId, updateDto, validToken);

        // then
        assertNotNull(result);
        assertEquals("Updated Product", sampleProductEntity.getName());
        assertEquals(99.99, sampleProductEntity.getPrice());
        assertEquals("Updated Desc", sampleProductEntity.getDescription());
        assertEquals(50, sampleProductEntity.getQuantityInStock());
        assertEquals(999, sampleProductEntity.getCategory().getCategoryId());
        assertEquals("http://example.com/new.jpg", sampleProductEntity.getImageUrl());
    }

    @Test
    void updateProduct_NonExistingId_ThrowsResourceNotFoundException() {
        // given
        int productId = 999;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.updateProduct(productId, sampleProductDto, validToken)
        );
        assertTrue(thrown.getMessage().contains("Product with id 999 not found"));
    }

    @Test
    void updateProduct_CategoryNotFound_ThrowsResourceNotFoundException() {
        // given
        int productId = 1;
        sampleProductDto.setCategoryId(1111);

        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProductEntity));
        when(categoryRepository.findById(1111)).thenReturn(Optional.empty());

        // when & then
        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.updateProduct(productId, sampleProductDto, validToken)
        );
        assertTrue(thrown.getMessage().contains("Category with id 1111 not found"));
    }

    @Test
    void updateProduct_AllFieldsNull_NoChanges() {
        // given
        int productId = 1;
        ProductDto updateDto = new ProductDto(
                1,
                null,
                null,
                null,
                null,
                123,
                null,
                null,
                null
        );
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(sampleProductEntity));

        // when
        productService.updateProduct(productId, updateDto, validToken);

        // then
        // All fields should remain the same as in sampleProductEntity
        assertEquals("Sample Product", sampleProductEntity.getName());
        assertEquals("Sample Desc", sampleProductEntity.getDescription());
        assertEquals(10.0, sampleProductEntity.getPrice());
        assertEquals(5, sampleProductEntity.getQuantityInStock());
        assertEquals("http://example.com/image.jpg", sampleProductEntity.getImageUrl());

        verify(productRepository).save(sampleProductEntity);
    }

    @Test
    void updateProduct_AllFieldsNonNull_UpdatesAllFields() {
        // given
        int productId = 1;
        ProductDto updateDto = new ProductDto(
                1,                      // id
                "New Name",             // name
                "New Desc",             // description
                99.99,                  // price
                100,                    // stockQuantity
                123,                    // sellerId
                999,                    // categoryId
                null,                   // dateAdded
                "http://example.com/new.jpg" // imageUrl
        );
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(sampleProductEntity));

        // mock category
        CategoryEntity newCategory = new CategoryEntity();
        newCategory.setCategoryId(999);
        when(categoryRepository.findById(999))
                .thenReturn(Optional.of(newCategory));

        when(productMapper.toDto(sampleProductEntity)).thenReturn(updateDto);

        // when
        ProductDto result = productService.updateProduct(productId, updateDto, validToken);

        // then
        assertEquals("New Name", sampleProductEntity.getName());
        assertEquals("New Desc", sampleProductEntity.getDescription());
        assertEquals(99.99, sampleProductEntity.getPrice());
        assertEquals(100, sampleProductEntity.getQuantityInStock());
        assertEquals(999, sampleProductEntity.getCategory().getCategoryId());
        assertEquals("http://example.com/new.jpg", sampleProductEntity.getImageUrl());
        verify(productRepository).save(sampleProductEntity);
        assertEquals("New Name", result.getName());
    }

    @Test
    void updateProduct_NameOnly_UpdatesName() {
        // given
        int productId = 1;
        ProductDto updateDto = new ProductDto(
                1,
                "Updated Name",
                null,
                null,
                null,
                123,
                null,
                null,
                null
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProductEntity));
        when(productMapper.toDto(sampleProductEntity)).thenReturn(updateDto);

        // when
        productService.updateProduct(productId, updateDto, validToken);

        // then
        assertEquals("Updated Name", sampleProductEntity.getName());
        assertEquals("Sample Desc", sampleProductEntity.getDescription()); // unchanged
        verify(productRepository).save(sampleProductEntity);
    }

    @Test
    void updateProduct_DescriptionOnly_UpdatesDescription() {
        int productId = 1;
        ProductDto updateDto = new ProductDto(
                1,
                null,
                "Updated Desc",
                null,
                null,
                123,
                null,
                null,
                null
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProductEntity));
        when(productMapper.toDto(sampleProductEntity)).thenReturn(updateDto);

        productService.updateProduct(productId, updateDto, validToken);

        assertEquals("Sample Product", sampleProductEntity.getName()); // unchanged
        assertEquals("Updated Desc", sampleProductEntity.getDescription());
        verify(productRepository).save(sampleProductEntity);
    }

    @Test
    void updateProduct_PriceOnly_UpdatesPrice() {
        int productId = 1;
        ProductDto updateDto = new ProductDto(
                1,
                null,
                null,
                1234.56,
                null,
                123,
                null,
                null,
                null
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProductEntity));
        when(productMapper.toDto(sampleProductEntity)).thenReturn(updateDto);

        productService.updateProduct(productId, updateDto, validToken);

        assertEquals(1234.56, sampleProductEntity.getPrice());
        assertEquals(5, sampleProductEntity.getQuantityInStock()); // unchanged
        verify(productRepository).save(sampleProductEntity);
    }

    @Test
    void updateProduct_StockQuantityOnly_UpdatesQuantity() {
        int productId = 1;
        ProductDto updateDto = new ProductDto(
                1,
                null,
                null,
                null,
                999,
                123,
                null,
                null,
                null
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProductEntity));
        when(productMapper.toDto(sampleProductEntity)).thenReturn(updateDto);

        productService.updateProduct(productId, updateDto, validToken);

        assertEquals(999, sampleProductEntity.getQuantityInStock());
        assertEquals(10.0, sampleProductEntity.getPrice()); // unchanged
        verify(productRepository).save(sampleProductEntity);
    }

    @Test
    void updateProduct_CategoryIdOnly_UpdatesCategory() {
        int productId = 1;
        ProductDto updateDto = new ProductDto(
                1,
                null,
                null,
                null,
                null,
                123,
                999, // category
                null,
                null
        );

        CategoryEntity newCategory = new CategoryEntity();
        newCategory.setCategoryId(999);

        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProductEntity));
        when(categoryRepository.findById(999)).thenReturn(Optional.of(newCategory));
        when(productMapper.toDto(sampleProductEntity)).thenReturn(updateDto);

        productService.updateProduct(productId, updateDto, validToken);

        assertEquals(999, sampleProductEntity.getCategory().getCategoryId());
        assertEquals(10.0, sampleProductEntity.getPrice()); // unchanged
        verify(productRepository).save(sampleProductEntity);
    }

    @Test
    void updateProduct_ImageUrlOnly_UpdatesImageUrl() {
        int productId = 1;
        ProductDto updateDto = new ProductDto(
                1,
                null,
                null,
                null,
                null,
                123,
                null,
                null,
                "http://example.com/new.png"
        );

        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProductEntity));
        when(productMapper.toDto(sampleProductEntity)).thenReturn(updateDto);

        productService.updateProduct(productId, updateDto, validToken);

        assertEquals("http://example.com/new.png", sampleProductEntity.getImageUrl());
        assertEquals(10.0, sampleProductEntity.getPrice()); // unchanged
        verify(productRepository).save(sampleProductEntity);
    }

    // ---------------------------------------------------------------------------------------------
    // deleteProduct
    // ---------------------------------------------------------------------------------------------
    @Test
    void deleteProduct_ExistingId_DeletesSuccessfully() {
        // given
        int productId = 1;
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProductEntity));
        when(validClaims.get("roles")).thenReturn(new ArrayList<>(List.of(new LinkedHashMap<>(Map.of("authority", "ROLE_USER")))));


        // when
        productService.deleteProduct(productId, validToken);

        // then
        verify(productRepository).delete(sampleProductEntity);
    }

    @Test
    void deleteProduct_NonExistingId_ThrowsResourceNotFoundException() {
        // given
        int productId = 999;

        // when & then
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.deleteProduct(productId, validToken)
        );

        assertTrue(thrown.getMessage().contains("Product with id 999 not found"));
        verify(productRepository, never()).delete(any(ProductEntity.class));
    }

    @Test
    void deleteProduct_AdminDeletes_DeletesSuccessfully() {
        // given
        int productId = 1;
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProductEntity));
        when(validClaims.get("userId")).thenReturn(999);
        when(validClaims.get("roles")).thenReturn(new ArrayList<>(List.of(new LinkedHashMap<>(Map.of("authority", "ROLE_ADMIN")))));

        // when
        productService.deleteProduct(productId, validToken);

        // then
        verify(productRepository).delete(sampleProductEntity);
    }
}
