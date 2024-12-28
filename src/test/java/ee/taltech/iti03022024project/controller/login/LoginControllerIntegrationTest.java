package ee.taltech.iti03022024project.controller.login;

import ee.taltech.iti03022024project.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class LoginControllerIntegrationTest extends AbstractIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(LoginControllerIntegrationTest.class);
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void LoginTestSuccessful() throws Exception {
        mockMvc.perform(
                        post("/api/public/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"userEmail1@gmail.com\",\"password\":\"userPassword\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").isNotEmpty());
    }

    @Test
    public void LoginTestUserNotExist() throws Exception {
        mockMvc.perform(
                        post("/api/public/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"wrongEmail@gmail.com\",\"password\":\"userPassword\"}")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.jwtToken").doesNotExist());
    }

    @Test
    public void LoginTestInvalidPassword() throws Exception {
        mockMvc.perform(
                        post("/api/public/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"userEmail1@gmail.com\",\"password\":\"invalidPassword\"}")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.jwtToken").doesNotExist());
    }
}
