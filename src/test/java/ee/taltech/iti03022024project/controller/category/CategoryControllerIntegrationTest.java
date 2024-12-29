package ee.taltech.iti03022024project.controller.category;

import ee.taltech.iti03022024project.AbstractIntegrationTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class CategoryControllerIntegrationTest extends AbstractIntegrationTest {

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
    void getCategories_NoFilters_ReturnsPaginatedCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void getCategoryById_CategoryExists_ReturnsCategory() throws Exception {
        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void getCategoryById_CategoryDoesNotExist_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCategory_ValidCategory_ReturnsCreatedCategory() throws Exception {
        String validCategoryJson = """
        {
            "id": null,
            "name": "New Category"
        }
        """;

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminJwtToken)
                        .content(validCategoryJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Category"));
    }

    @Test
    void createCategory_ValidCategoryNotAdmin_ReturnsForbidden() throws Exception {
        String validCategoryJson = """
        {
            "id": null,
            "name": "New Category"
        }
        """;

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(validCategoryJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void createCategory_CategoryWithIdSet_ReturnsCreatedCategoryWithoutIdViolates() throws Exception {
        String validCategoryJson = """
        {
            "id": 1000,
            "name": "New Category"
        }
        """;

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminJwtToken)
                        .content(validCategoryJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(not(1000)))
                .andExpect(jsonPath("$.name").value("New Category"));
    }

    @Test
    void createCategory_InvalidCategory_ReturnsValidationError() throws Exception {
        String longName = "a".repeat(256);

        String invalidCategoryJson = """
            {
                "id": null,
                "name": "%s"
            }
            """.formatted(longName);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminJwtToken)
                        .content(invalidCategoryJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCategory_InvalidCategory_ReturnsValidationError() throws Exception {
        String longName = "a".repeat(256);

        String invalidCategoryJson = """
    {
        "id": null,
        "name": "%s"
    }
    """.formatted(longName);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminJwtToken)
                        .content(invalidCategoryJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateCategory_CategoryExists_ReturnsUpdatedCategory() throws Exception {
        String updatedCategoryJson = """
        {
            "id": null,
            "name": "Updated Category"
        }
        """;

        mockMvc.perform(patch("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminJwtToken)
                        .content(updatedCategoryJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Category"));
    }

    @Test
    void updateCategory_CategoryDoesNotExist_ReturnsNotFound() throws Exception {
        String updatedCategoryJson = """
        {
            "id": null,
            "name": "Non-Existent Category"
        }
        """;

        mockMvc.perform(patch("/api/categories/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminJwtToken)
                        .content(updatedCategoryJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCategory_NotAdmin_ReturnsForbidden() throws Exception {
        String updatedCategoryJson = """
        {
            "id": null,
            "name": "Updated Category"
        }
        """;

        mockMvc.perform(patch("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(updatedCategoryJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteCategory_CategoryExists_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/categories/1")
                .header("Authorization", "Bearer " + adminJwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCategory_CategoryDoesNotExist_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/categories/999")
                .header("Authorization", "Bearer " + adminJwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCategory_NotAdmin_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isUnauthorized());
    }
}
