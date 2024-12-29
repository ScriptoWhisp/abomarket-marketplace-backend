package ee.taltech.iti03022024project.controller.product;

import ee.taltech.iti03022024project.AbstractIntegrationTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    private String jwtToken;
    private String adminJwtToken;

    @BeforeEach
    public void setUp() throws Exception {
        String tokenResponse = mockMvc.perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"userEmail1@gmail.com\",\"password\":\"userPassword\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        this.jwtToken = tokenResponse.substring(tokenResponse.indexOf(":\"") + 2, tokenResponse.indexOf("\"}"));

        String adminTokenResponse = mockMvc.perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"userEmail3@gmail.com\",\"password\":\"userPassword\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        this.adminJwtToken = adminTokenResponse.substring(adminTokenResponse.indexOf(":\"") + 2, adminTokenResponse.indexOf("\"}"));
    }

    @Test
    void getProducts_ProductsExist_ReturnProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3));
    }

    @Test
    void getProductById_ProductExists_ReturnProduct() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("product1"))
                .andExpect(jsonPath("$.description").value("productDescription1"))
                .andExpect(jsonPath("$.stockQuantity").value(1))
                .andExpect(jsonPath("$.price").value(100))
                .andExpect(jsonPath("$.sellerId").value(1))
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.dateAdded").value("2024-12-20T00:00:00Z"));
    }

    @Test
    void getProductsById_ProductDoesNotExist_ReturnStatusNotFound() throws Exception {
        mockMvc.perform(get("/api/products/4"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProductsByUserId_ProductsExist_ReturnProducts() throws Exception {
        mockMvc.perform(get("/api/products/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void getProductsByUserId_UserDoesNotExist_ReturnStatusNotFound() throws Exception {
        mockMvc.perform(get("/api/products/user/4"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_ProductCreated_ReturnProduct() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"productName\",\"description\":\"productDescription\",\"stockQuantity\":1,\"price\":1.0,\"sellerId\":1,\"categoryId\":2}")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("productName"))
                .andExpect(jsonPath("$.description").value("productDescription"))
                .andExpect(jsonPath("$.stockQuantity").value(1))
                .andExpect(jsonPath("$.price").value(1.0))
                .andExpect(jsonPath("$.sellerId").value(1))
                .andExpect(jsonPath("$.categoryId").value(2));
    }

    @Test
    void createProduct_UserNotAuthorized_ReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"productName\",\"description\":\"productDescription\",\"stockQuantity\":1,\"price\":1.0,\"sellerId\":1,\"categoryId\":2}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateProduct_ProductUpdated_ReturnProduct() throws Exception {
        mockMvc.perform(patch("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"updatedProductName\"}")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("updatedProductName"))
                .andExpect(jsonPath("$.description").value("productDescription1"))
                .andExpect(jsonPath("$.stockQuantity").value(1))
                .andExpect(jsonPath("$.price").value(100))
                .andExpect(jsonPath("$.sellerId").value(1))
                .andExpect(jsonPath("$.categoryId").value(1));
    }

    @Test
    void updateProduct_ProductDoesNotExist_ReturnStatusNotFound() throws Exception {
        mockMvc.perform(patch("/api/products/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"updatedProductName\"}")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProduct_UserNotAuthorized_ReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(patch("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"updatedProductName\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateProduct_UserAndSellerIdsDoNotMatch_ReturnStatusForbidden() throws Exception {
        mockMvc.perform(patch("/api/products/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"updatedProductName\",\"description\":\"updatedProductDescription\",\"stockQuantity\":1,\"price\":2.0,\"CategoryId\":2}")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteProduct_ProductDeleted_ReturnStatusNoContent() throws Exception {
        mockMvc.perform(delete("/api/products/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_ProductDoesNotExist_ReturnStatusNotFound() throws Exception {
        mockMvc.perform(delete("/api/products/4")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_UserNotAuthorized_ReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteProduct_UserAndSellerIdsDoNotMatch_ReturnStatusForbidden() throws Exception {
        mockMvc.perform(delete("/api/products/3")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteProduct_AdminDeletesProduct_ReturnStatusNoContent() throws Exception {
        mockMvc.perform(delete("/api/products/1")
                        .header("Authorization", "Bearer " + adminJwtToken))
                .andExpect(status().isNoContent());
    }

}

