package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.AbstractIntegrationTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest extends AbstractIntegrationTest {
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
    void getAllUsers_UsersExist_ReturnAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(4));
    }

    @Test
    void getUserById_UserWithId2Exists_ReturnUser() throws Exception {
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
    void getUserById_UserWithId4DoesNotExist_ReturnStatusNotFound() throws Exception {
        mockMvc.perform(get("/api/users/4"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAuthorizedUser_GetAuthorizedUserByToken_ReturnAuthorizedUser() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void getAuthorizedUser_UserIsNotAuthorized_ReturnStatusForbidden() throws Exception {
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    void patchAuthorizedUser_PatchAuthorizedUserByToken_ReturnPatchedAuthorizedUser() throws Exception {
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
                .andExpect(jsonPath("$.location").exists());
    }

    @Test
    void patchAuthorizedUser_PatchUserWhichIsNotAuthorized_ReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(patch("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"location\":\"updatedLocation\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void patchUserById_AdminPatchesUserById_ReturnPatchedUser() throws Exception {
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
                .andExpect(jsonPath("$.location").exists());
    }

    @Test
    void patchUserById_AdminPatchesUserWhichDoesNotExist_ReturnStatusNotFound() throws Exception {
        mockMvc.perform(patch("/api/users/4")
                        .header("Authorization", "Bearer " + adminJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"updatedByAdminUserName4\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchUserById_NotAdminPatchesUserById_ReturnStatusForbidden() throws Exception {
        mockMvc.perform(patch("/api/users/2")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"updatedUserName2\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void patchUserById_UnauthorizedUserPatchesUserById_ReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(patch("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"updatedUserName2\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUser_NotAdminDeletesUser_ReturnStatusForbidden() throws Exception {
        mockMvc.perform(delete("/api/users/2")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUser_AdminDeletesUser_ReturnStatusNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/2")
                        .header("Authorization", "Bearer " + adminJwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_UserDoesNotExist_ReturnStatusNotFound() throws Exception {
        mockMvc.perform(delete("/api/users/4")
                        .header("Authorization", "Bearer " + adminJwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_UnauthorizedUserDeletesUser_ReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/users/2"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createUser_CreatesUser_ReturnNewUser() throws Exception {
        String newUserJson = """
        {
            "firstName": "userName4",
            "lastName": "userLastName4",
            "email": "userEmail4@gmail.com",
            "password": "userPassword",
            "phone": "userPhone4",
            "location": "userLocation4"
        }
        """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.firstName").value("userName4"))
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$.phone").exists())
                .andExpect(jsonPath("$.location").exists());
    }

    @Test
    void createUser_CreatesUserWithRegisteredEmail_returnInternalServerError() throws Exception {
        String newUserJson = """
        {
            "firstName": "userName4",
            "lastName": "userLastName4",
            "email": "userEmail1@gmail.com",
            "password": "userPassword",
            "phone": "userPhone4",
            "location": "userLocation4"
        }
        """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isInternalServerError());
    }
}
