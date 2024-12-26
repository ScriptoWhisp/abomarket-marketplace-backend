package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.criteria.OrderSearchCriteria;
import ee.taltech.iti03022024project.domain.OrderEntity;
import ee.taltech.iti03022024project.domain.StatusEntity;
import ee.taltech.iti03022024project.domain.UserEntity;
import ee.taltech.iti03022024project.dto.OrderDto;
import ee.taltech.iti03022024project.exception.ObjectCreationException;
import ee.taltech.iti03022024project.exception.ResourceNotFoundException;
import ee.taltech.iti03022024project.mapstruct.OrderMapper;
import ee.taltech.iti03022024project.repository.OrderRepository;
import ee.taltech.iti03022024project.repository.StatusRepository;
import ee.taltech.iti03022024project.responses.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private StatusRepository statusRepository;
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private OrderEntity orderEntity;
    private OrderDto orderDto;
    private StatusEntity statusEntity;

    @BeforeEach
    void setUp() {
        orderEntity = new OrderEntity();
        orderEntity.setOrderId(10);

        orderDto = OrderDto.builder().id(10).statusId(1).build();

        statusEntity = new StatusEntity();
        statusEntity.setStatusId(1);
        statusEntity.setStatusName("READY");

        lenient().when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(orderEntity);
        lenient().when(orderMapper.toDto(any(OrderEntity.class))).thenReturn(orderDto);
    }

    // ----------------------------------------------------------------------------------------
    // getOrders
    // ----------------------------------------------------------------------------------------
    @Test
    void getOrders_ValidCriteria_ReturnsPagedOrders() {
        // given
        OrderSearchCriteria criteria = new OrderSearchCriteria(10, 100, 1); // id=10, userId=100, statusId=1
        int pageNo = 0;
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        Page<OrderEntity> mockPage =
                new PageImpl<>(Collections.singletonList(orderEntity), pageRequest, 1);

        when(orderRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(mockPage);

        // when
        PageResponse<OrderDto> response = orderService.getOrders(criteria, pageNo, pageSize);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().getFirst().getId()).isEqualTo(10);
        verify(orderRepository).findAll(any(Specification.class), eq(pageRequest));
    }

    @Test
    void getOrders_InvalidPagination_UsesDefaults() {
        // given
        // pageNo < 0 => should default to 0
        // pageSize < 1 => should default to 1
        OrderSearchCriteria criteria = new OrderSearchCriteria(null, null, null);
        int negativePageNo = -5;
        int zeroPageSize = 0;

        PageRequest expectedPaging = PageRequest.of(0, 1);

        Page<OrderEntity> mockPage =
                new PageImpl<>(Collections.emptyList(), expectedPaging, 0);

        when(orderRepository.findAll(any(Specification.class), eq(expectedPaging))).thenReturn(mockPage);

        // when
        PageResponse<OrderDto> response = orderService.getOrders(criteria, negativePageNo, zeroPageSize);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).isEmpty();
        verify(orderRepository).findAll(any(Specification.class), eq(expectedPaging));
    }

    // ----------------------------------------------------------------------------------------
    // getOrderById
    // ----------------------------------------------------------------------------------------
    @Test
    void getOrderById_OrderExists_ReturnsOrder() {
        // given
        when(orderRepository.findById(10)).thenReturn(Optional.of(orderEntity));

        // when
        OrderDto actualOrder = orderService.getOrderById(10);

        // then
        assertThat(actualOrder).isNotNull();
        assertThat(actualOrder.getId()).isEqualTo(10);
        verify(orderRepository).findById(10);
    }

    @Test
    void getOrderById_OrderNotFound_ThrowsResourceNotFoundException() {
        // given
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> orderService.getOrderById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order with id 999 not found");
        verify(orderRepository).findById(999);
    }

    // ----------------------------------------------------------------------------------------
    // createOrder
    // ----------------------------------------------------------------------------------------
    @Test
    void createOrder_ValidOrder_CreatesSuccessfully() {
        // given
        when(statusRepository.findById(1)).thenReturn(Optional.of(statusEntity));
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(inv -> inv.getArgument(0)); // mimic save

        // when
        OrderDto createdOrder = orderService.createOrder(orderDto);

        // then
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getId()).isEqualTo(10);
        verify(statusRepository).findById(1);
        verify(orderRepository).save(orderEntity);
    }

    @Test
    void createOrder_StatusNotFound_ThrowsResourceNotFoundException() {
        // given
        when(statusRepository.findById(1)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> orderService.createOrder(orderDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Status with id 1 not found");
        verify(statusRepository).findById(1);
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void createOrder_ExceptionInProcess_ThrowsObjectCreationException() {
        // given
        when(statusRepository.findById(1)).thenThrow(new RuntimeException("DB error"));

        // when / then
        assertThatThrownBy(() -> orderService.createOrder(orderDto))
                .isInstanceOf(ObjectCreationException.class)
                .hasMessageContaining("Failed to create order: DB error");
        verify(statusRepository).findById(1);
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    // ----------------------------------------------------------------------------------------
    // createUnfinishedOrderForUser
    // ----------------------------------------------------------------------------------------
    @Test
    void createUnfinishedOrderForUser_ValidUser_CreatesCart() {
        // given
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(100);
        StatusEntity cartStatus = new StatusEntity();
        cartStatus.setStatusId(0);   // ID=0 => "cart" status

        when(statusRepository.getReferenceById(0)).thenReturn(cartStatus);
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        OrderEntity cartOrder = orderService.createUnfinishedOrderForUser(userEntity);

        // then
        assertThat(cartOrder).isNotNull();
        assertThat(cartOrder.getUser()).isEqualTo(userEntity);
        assertThat(cartOrder.getStatus().getStatusId()).isZero();
        verify(statusRepository).getReferenceById(0);
        verify(orderRepository).save(any(OrderEntity.class));
    }

    @Test
    void createUnfinishedOrderForUser_ExceptionThrown_ThrowsObjectCreationException() {
        // given
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(100);
        when(statusRepository.getReferenceById(0)).thenThrow(new RuntimeException("Could not fetch reference"));

        // when / then
        assertThatThrownBy(() -> orderService.createUnfinishedOrderForUser(userEntity))
                .isInstanceOf(ObjectCreationException.class)
                .hasMessageContaining("Failed to create cart: Could not fetch reference");
        verify(statusRepository).getReferenceById(0);
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    // ----------------------------------------------------------------------------------------
    // updateOrder
    // ----------------------------------------------------------------------------------------
    @Test
    void updateOrder_ValidStatus_UpdatesSuccessfully() {
        // given
        when(orderRepository.findById(10)).thenReturn(Optional.of(orderEntity));
        when(statusRepository.findById(1)).thenReturn(Optional.of(statusEntity));
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        OrderDto updatedOrder = orderService.updateOrder(10, orderDto);

        // then
        assertThat(updatedOrder).isNotNull();
        assertThat(updatedOrder.getId()).isEqualTo(10);
        verify(orderRepository).findById(10);
        verify(statusRepository).findById(1);
        verify(orderRepository).save(orderEntity);
    }

    @Test
    void updateOrder_OrderNotFound_ThrowsResourceNotFoundException() {
        // given
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> orderService.updateOrder(999, orderDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order with id 999 not found");
        verify(orderRepository).findById(999);
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void updateOrder_StatusNotFound_ThrowsResourceNotFoundException() {
        // given
        when(orderRepository.findById(10)).thenReturn(Optional.of(orderEntity));
        when(statusRepository.findById(1)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> orderService.updateOrder(10, orderDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Status with id 1 not found");
        verify(orderRepository).findById(10);
        verify(statusRepository).findById(1);
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    // ----------------------------------------------------------------------------------------
    // deleteOrder
    // ----------------------------------------------------------------------------------------
    @Test
    void deleteOrder_OrderExists_DeletesSuccessfully() {
        // given
        when(orderRepository.findById(10)).thenReturn(Optional.of(orderEntity));

        // when
        orderService.deleteOrder(10);

        // then
        verify(orderRepository).findById(10);
        verify(orderRepository).delete(orderEntity);
    }

    @Test
    void deleteOrder_OrderNotFound_ThrowsResourceNotFoundException() {
        // given
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> orderService.deleteOrder(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order with id 999 not found");
        verify(orderRepository).findById(999);
        verify(orderRepository, never()).delete(any(OrderEntity.class));
    }
}
