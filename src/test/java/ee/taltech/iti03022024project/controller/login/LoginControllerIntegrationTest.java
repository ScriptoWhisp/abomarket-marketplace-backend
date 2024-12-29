package ee.taltech.iti03022024project.controller.login;

import ee.taltech.iti03022024project.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void Login_LoginUser_ReturnJwt() throws Exception {
        mockMvc.perform(
                        post("/api/public/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"userEmail1@gmail.com\",\"password\":\"userPassword\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").isNotEmpty());
    }

    @Test
    void Login_LoginUserWhichDoesNotExist_ReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(
                        post("/api/public/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"wrongEmail@gmail.com\",\"password\":\"userPassword\"}")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.jwtToken").doesNotExist());
    }

    @Test
    void Login_LoginUserWithInvalidPassword_ReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(
                        post("/api/public/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"userEmail1@gmail.com\",\"password\":\"invalidPassword\"}")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.jwtToken").doesNotExist());
    }
}
