package ee.taltech.iti03022024project.controller.status;

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
class StatusControllerIntegrationTest extends AbstractIntegrationTest {

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
    void getStatuses_NoFilters_ReturnsPaginatedStatuses() throws Exception {
        mockMvc.perform(get("/api/statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void getStatusById_StatusExists_ReturnsStatus() throws Exception {
        mockMvc.perform(get("/api/statuses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void getStatusById_StatusDoesNotExist_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/statuses/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createStatus_ValidStatus_ReturnsCreatedStatus() throws Exception {
        String validStatusJson = """
        {
            "id": null,
            "name": "New Status"
        }
        """;

        mockMvc.perform(post("/api/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminJwtToken)
                        .content(validStatusJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Status"));
    }

    @Test
    void createStatus_ValidStatusNotAdmin_ReturnsForbidden() throws Exception {
        String validStatusJson = """
        {
            "id": null,
            "name": "New Status"
        }
        """;

        mockMvc.perform(post("/api/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(validStatusJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void createStatus_StatusWithIdSet_ReturnsCreatedStatusWithoutIdViolates() throws Exception {
        String validStatusJson = """
        {
            "id": 1000,
            "name": "New Status"
        }
        """;

        mockMvc.perform(post("/api/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminJwtToken)
                        .content(validStatusJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(not(1000)))
                .andExpect(jsonPath("$.name").value("New Status"));
    }

    @Test
    void createStatus_InvalidStatus_ReturnsValidationError() throws Exception {
        String longName = "a".repeat(256);

        String invalidStatusJson = """
            {
                "id": null,
                "name": "%s"
            }
            """.formatted(longName);

        mockMvc.perform(post("/api/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminJwtToken)
                        .content(invalidStatusJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_InvalidStatus_ReturnsValidationError() throws Exception {
        String longName = "a".repeat(256);

        String invalidStatusJson = """
        {
            "id": null,
            "name": "%s"
        }
        """.formatted(longName);

        mockMvc.perform(patch("/api/statuses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminJwtToken)
                        .content(invalidStatusJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_StatusExists_ReturnsUpdatedStatus() throws Exception {
        String updatedStatusJson = """
        {
            "id": null,
            "name": "Updated Status"
        }
        """;

        mockMvc.perform(patch("/api/statuses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminJwtToken)
                        .content(updatedStatusJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Status"));
    }

    @Test
    void updateStatus_StatusDoesNotExist_ReturnsNotFound() throws Exception {
        String updatedStatusJson = """
        {
            "id": null,
            "name": "Non-Existent Status"
        }
        """;

        mockMvc.perform(patch("/api/statuses/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminJwtToken)
                        .content(updatedStatusJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatus_NotAdmin_ReturnsForbidden() throws Exception {
        String updatedStatusJson = """
        {
            "id": null,
            "name": "Updated Status"
        }
        """;

        mockMvc.perform(patch("/api/statuses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(updatedStatusJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteStatus_StatusExists_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/statuses/1")
                        .header("Authorization", "Bearer " + adminJwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteStatus_StatusDoesNotExist_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/statuses/999")
                        .header("Authorization", "Bearer " + adminJwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStatus_NotAdmin_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/statuses/1"))
                .andExpect(status().isUnauthorized());
    }
}
