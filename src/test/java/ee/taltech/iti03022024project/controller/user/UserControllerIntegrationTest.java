package ee.taltech.iti03022024project.controller.user;

import ee.taltech.iti03022024project.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest extends AbstractIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(UserControllerIntegrationTest.class);
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
    public void getAllUsersTest() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3));
    }

    @Test
    public void getUserByIdTestSuccessful() throws Exception {
        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.firstName").value("userName2"))
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$.phone").exists())
                .andExpect(jsonPath("$.location").exists())
                .andExpect(jsonPath("$.unfinishedOrderId").value(nullValue()));
    }

    @Test
    public void getUserByIdTestUserNotFound() throws Exception {
        mockMvc.perform(get("/api/users/4"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAuthorizedUserSuccessful() throws Exception {
        // 2. Выполняем запрос с токеном для доступа к защищенному ресурсу
        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void getAuthorizedUserUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void patchAuthorizedUserSuccessful() throws Exception {
        mockMvc.perform(patch("/api/users/profile")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"updatedUserName1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("updatedUserName1"))
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$.phone").exists())
                .andExpect(jsonPath("$.location").exists())
                .andExpect(jsonPath("$.unfinishedOrderId").value(nullValue()));
    }

    @Test
    public void patchAuthorizedUserNotAuthorized() throws Exception {
        mockMvc.perform(patch("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"location\":\"updatedLocation\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void patchUserByIdSuccessful() throws Exception {
        mockMvc.perform(patch("/api/users/1")
                        .header("Authorization", "Bearer " + adminJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"updatedByAdminUserName1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("updatedByAdminUserName1"))
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$.phone").exists())
                .andExpect(jsonPath("$.location").exists())
                .andExpect(jsonPath("$.unfinishedOrderId").value(nullValue()));
    }

    @Test
    public void patchUserByIdUserNotFound() throws Exception {
        mockMvc.perform(patch("/api/users/4")
                        .header("Authorization", "Bearer " + adminJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"updatedByAdminUserName4\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void patchUserByIdUserNotAdmin() throws Exception {
        mockMvc.perform(patch("/api/users/2")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"updatedUserName2\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteUserNotAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/2")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteUserAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/2")
                        .header("Authorization", "Bearer " + adminJwtToken))
                .andExpect(status().isNoContent());
    }


}
