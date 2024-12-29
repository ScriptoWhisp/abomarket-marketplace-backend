package ee.taltech.iti03022024project.controller.order;

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
class OrderControllerIntegrationTest extends AbstractIntegrationTest {
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
    void getOrders_OrdersExist_ReturnOrders() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3));
    }

    @Test
    void getOrders_OrdersExistUserNotAuthorized_ReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getOrderById_OrderWithId2Exists_ReturnOrder() throws Exception {
        mockMvc.perform(get("/api/orders/2")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void getOrderById_OrderWithId2ExistsUserNotAuthorized_ReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(get("/api/orders/2"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getOrderById_OrderWithId4DoesNotExist_ReturnStatusNotFound() throws Exception {
        mockMvc.perform(get("/api/orders/4")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void createOrder_CreateOrder_ReturnOrder() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"statusId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4));
    }

    @Test
    void createOrder_CreateOrderUserNotAuthorized_ReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"statusId\":1}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateOrderStatus_AdminUpdatesOrderStatus_ReturnUpdatedOrder() throws Exception {
        mockMvc.perform(patch("/api/orders/1")
                        .header("Authorization", "Bearer " + adminJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statusId\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statusId").value(2));
    }

    @Test
    void updateOrderStatus_NotAdminUpdatesOrderStatus_ReturnUpdatedOrder() throws Exception {
        mockMvc.perform(patch("/api/orders/1")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statusId\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statusId").value(2));
    }

    @Test
    void updateOrderStatus_UserUpdatesAnotherUserOrderStatus_ReturnStatusForbidden() throws Exception {
        mockMvc.perform(patch("/api/orders/3")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statusId\":2}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateOrderStatus_OrderDoesNotExist_ReturnStatusNotFound() throws Exception {
        mockMvc.perform(patch("/api/orders/4")
                        .header("Authorization", "Bearer " + adminJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statusId\":2}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOrderStatus_UserNotAuthorized_ReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(patch("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statusId\":2}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteOrder_AdminDeletesOrder_ReturnStatusNoContent() throws Exception {
        mockMvc.perform(delete("/api/orders/1")
                        .header("Authorization", "Bearer " + adminJwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteOrder_NotAdminDeletesOrder_ReturnStatusForbidden() throws Exception {
        mockMvc.perform(delete("/api/orders/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteOrder_OrderDoesNotExist_ReturnStatusNotFound() throws Exception {
        mockMvc.perform(delete("/api/orders/4")
                        .header("Authorization", "Bearer " + adminJwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOrder_UserNotAuthorized_ReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isUnauthorized());
    }
}
