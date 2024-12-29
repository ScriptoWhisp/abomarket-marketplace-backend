package ee.taltech.iti03022024project.controller.orderItem;

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
class OrderItemControllerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    private String jwtToken;

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
    }

    @Test
    void getOrderItems_OrderItemsExist_ReturnOrderItems() throws Exception {
        mockMvc.perform(get("/api/order_items")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void getOrderItemById_OrderItemExists_ReturnOrderItem() throws Exception {
        mockMvc.perform(get("/api/order_items/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(1))
                .andExpect(jsonPath("$.priceAtTimeOfOrder").value(100));
    }

    @Test
    void getOrderItemById_OrderItemDoesNotExist_ReturnStatusNotFound() throws Exception {
        mockMvc.perform(get("/api/order_items/3")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void createOrderItem_CreateOrderItem_ReturnCreatedOrderItem() throws Exception {
        mockMvc.perform(post("/api/order_items")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":1,\"productId\":2,\"quantity\":3,\"priceAtTimeOfOrder\":20.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.productId").value(2))
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.priceAtTimeOfOrder").value(20.0));
    }

    @Test
    void createOrderItem_InvalidOrderItem_ReturnStatusBadRequest() throws Exception {
        mockMvc.perform(post("/api/order_items")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":1,\"productId\":2,\"quantity\":-3,\"priceAtTimeOfOrder\":20.0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrderItem_UnauthorizedUserCreatesOrder_ReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(post("/api/order_items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":1,\"productId\":2,\"quantity\":3,\"priceAtTimeOfOrder\":20.0}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteOrderItem_OrderItemExists_ReturnStatusNoContent() throws Exception {
        mockMvc.perform(delete("/api/order_items/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteOrderItem_OrderItemDoesNotExist_ReturnStatusNotFound() throws Exception {
        mockMvc.perform(delete("/api/order_items/100")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOrderItem_Unauthorized_ReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/order_items/1"))
                .andExpect(status().isUnauthorized());
    }

}
