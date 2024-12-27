package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.criteria.OrderItemSearchCriteria;
import ee.taltech.iti03022024project.domain.OrderEntity;
import ee.taltech.iti03022024project.domain.OrderItemEntity;
import ee.taltech.iti03022024project.domain.ProductEntity;
import ee.taltech.iti03022024project.dto.OrderItemDto;
import ee.taltech.iti03022024project.exception.ObjectCreationException;
import ee.taltech.iti03022024project.exception.ResourceNotFoundException;
import ee.taltech.iti03022024project.mapstruct.OrderItemMapper;
import ee.taltech.iti03022024project.repository.OrderItemRepository;
import ee.taltech.iti03022024project.responses.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderItemService orderItemService;


    private OrderItemEntity orderItemEntity;
    private OrderItemDto orderItemDto;
    private List<OrderItemEntity> mockDatabase;

    @BeforeEach
    void setUp() {
        // Mock database entities
        OrderItemEntity entity1 = new OrderItemEntity();
        entity1.setOrderItemId(1);
        entity1.setQuantity(10);
        entity1.setPriceAtTimeOfOrder(99.99);
        ProductEntity product1 = new ProductEntity();
        product1.setProductId(1);
        entity1.setProduct(product1);
        OrderEntity order1 = new OrderEntity();
        order1.setOrderId(100);
        entity1.setOrder(order1);

        OrderItemEntity entity2 = new OrderItemEntity();
        entity2.setOrderItemId(2);
        entity2.setQuantity(5);
        entity2.setPriceAtTimeOfOrder(49.99);
        OrderEntity order2 = new OrderEntity();
        order2.setOrderId(200);
        entity2.setOrder(order2);

        OrderItemEntity entity3 = new OrderItemEntity();
        entity3.setOrderItemId(3);
        entity3.setQuantity(7);
        entity3.setPriceAtTimeOfOrder(29.99);
        OrderEntity order3 = new OrderEntity();
        order3.setOrderId(300);
        entity3.setOrder(order3);

        orderItemEntity = entity1;

        mockDatabase = List.of(entity1, entity2, entity3);

        // Mock DTOs
        OrderItemDto dto1 = OrderItemDto.builder().id(1).quantity(10).priceAtTimeOfOrder(99.99).orderId(100).build();
        OrderItemDto dto2 = OrderItemDto.builder().id(2).quantity(5).priceAtTimeOfOrder(49.99).orderId(200).build();
        OrderItemDto dto3 = OrderItemDto.builder().id(3).quantity(7).priceAtTimeOfOrder(29.99).orderId(300).build();

        orderItemDto = dto1;

        // Mapper configuration
        lenient().when(orderItemMapper.toDto(entity1)).thenReturn(dto1);
        lenient().when(orderItemMapper.toDto(entity2)).thenReturn(dto2);
        lenient().when(orderItemMapper.toDto(entity3)).thenReturn(dto3);
        lenient().when(orderItemMapper.toEntity(dto1)).thenReturn(entity1);
    }

    // ----------------------------------------------------------------------------------------
    // getOrderItems
    // ----------------------------------------------------------------------------------------
    @Test
    void getOrderItems_ValidCriteria_ReturnsPagedOrderItems() {
        // given
        OrderItemSearchCriteria criteria = OrderItemSearchCriteria.builder().id(1).build();
        int pageNo = 0;
        int pageSize = 2;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        Page<OrderItemEntity> mockPage = new PageImpl<>(
                Collections.singletonList(orderItemEntity), pageRequest, 1
        );

        when(orderItemRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(mockPage);

        // when
        PageResponse<OrderItemDto> response = orderItemService.getOrderItems(criteria, pageNo, pageSize);

        // then
        System.out.println(response);
        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().getFirst().getId()).isEqualTo(1);
        verify(orderItemRepository).findAll(any(Specification.class), eq(pageRequest));
    }

    @Test
    void getOrderItems_InvalidPagination_UsesDefaults() {
        // given
        OrderItemSearchCriteria criteria = OrderItemSearchCriteria.builder().build();
        int negativePageNo = -5;
        int zeroPageSize = 0;

        PageRequest expectedRequest = PageRequest.of(0, 1); // default corrected to page=0, size=1

        Page<OrderItemEntity> emptyPage = new PageImpl<>(Collections.emptyList(), expectedRequest, 0);

        when(orderItemRepository.findAll(any(Specification.class), eq(expectedRequest))).thenReturn(emptyPage);

        // when
        PageResponse<OrderItemDto> response = orderItemService.getOrderItems(criteria, negativePageNo, zeroPageSize);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).isEmpty();
        verify(orderItemRepository).findAll(any(Specification.class), eq(expectedRequest));
    }

    @Test
    void getOrderItems_OrderIdCriteria_ReturnsFilteredOrderItems() {
        // given
        OrderItemSearchCriteria criteria = new OrderItemSearchCriteria(null, 100, null, null, null);
        int pageNo = 0;
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        // Filter the mock database
        List<OrderItemEntity> filteredEntities = mockDatabase.stream()
                .filter(entity -> entity.getOrder().getOrderId() == criteria.orderId())
                .toList();

        Page<OrderItemEntity> mockPage = new PageImpl<>(filteredEntities, pageRequest, filteredEntities.size());
        when(orderItemRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(mockPage);

        // when
        PageResponse<OrderItemDto> response = orderItemService.getOrderItems(criteria, pageNo, pageSize);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(filteredEntities.size());
        assertThat(response.content().get(0).getOrderId()).isEqualTo(100);
        verify(orderItemRepository).findAll(any(Specification.class), eq(pageRequest));
    }

    @Test
    void getOrderItems_QuantityCriteria_ReturnsFilteredOrderItems() {
        // given
        OrderItemSearchCriteria criteria = new OrderItemSearchCriteria(null, null, null, 5, null);
        int pageNo = 0;
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        // Filter the mock database
        List<OrderItemEntity> filteredEntities = mockDatabase.stream()
                .filter(entity -> entity.getQuantity() == criteria.quantity())
                .toList();

        Page<OrderItemEntity> mockPage = new PageImpl<>(filteredEntities, pageRequest, filteredEntities.size());
        when(orderItemRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(mockPage);

        // when
        PageResponse<OrderItemDto> response = orderItemService.getOrderItems(criteria, pageNo, pageSize);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(filteredEntities.size());
        assertThat(response.content().get(0).getQuantity()).isEqualTo(5);
        verify(orderItemRepository).findAll(any(Specification.class), eq(pageRequest));
    }

    @Test
    void getOrderItems_PriceAtTimeOfOrderCriteria_ReturnsFilteredOrderItems() {
        // given
        OrderItemSearchCriteria criteria = new OrderItemSearchCriteria(null, null, null, null, 29.99);
        int pageNo = 0;
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        // Filter the mock database
        List<OrderItemEntity> filteredEntities = mockDatabase.stream()
                .filter(entity -> entity.getPriceAtTimeOfOrder() == criteria.priceAtTimeOfOrder())
                .toList();

        Page<OrderItemEntity> mockPage = new PageImpl<>(filteredEntities, pageRequest, filteredEntities.size());
        when(orderItemRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(mockPage);

        // when
        PageResponse<OrderItemDto> response = orderItemService.getOrderItems(criteria, pageNo, pageSize);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(filteredEntities.size());
        assertThat(response.content().get(0).getPriceAtTimeOfOrder()).isEqualTo(29.99);
        verify(orderItemRepository).findAll(any(Specification.class), eq(pageRequest));
    }

    @Test
    void getOrderItems_AllCriteriaNull_ReturnsAllOrderItems() {
        // given
        OrderItemSearchCriteria criteria = new OrderItemSearchCriteria(null, null, null, null, null);
        int pageNo = 0;
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        Page<OrderItemEntity> mockPage = new PageImpl<>(mockDatabase, pageRequest, mockDatabase.size());
        when(orderItemRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(mockPage);

        // when
        PageResponse<OrderItemDto> response = orderItemService.getOrderItems(criteria, pageNo, pageSize);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(mockDatabase.size());
        verify(orderItemRepository).findAll(any(Specification.class), eq(pageRequest));
    }


    @Test
    void getOrderItems_NegativePageNo_DefaultsToZero() {
        // given
        OrderItemSearchCriteria criteria = new OrderItemSearchCriteria(null, null, null, null, null);
        int pageNo = -1;
        int pageSize = 2;
        PageRequest expectedRequest = PageRequest.of(0, 2); // corrected to page=0

        when(orderItemRepository.findAll(any(Specification.class), eq(expectedRequest)))
                .thenReturn(Page.empty());

        // when
        PageResponse<OrderItemDto> response = orderItemService.getOrderItems(criteria, pageNo, pageSize);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).isEmpty();
        verify(orderItemRepository).findAll(any(Specification.class), eq(expectedRequest));
    }

    @Test
    void getOrderItems_PageSizeLessThanOne_DefaultsToOne() {
        // given
        OrderItemSearchCriteria criteria = new OrderItemSearchCriteria(null, null, null, null, null);
        int pageNo = 0;
        int pageSize = 0; // invalid
        PageRequest expectedRequest = PageRequest.of(0, 1); // corrected to size=1

        when(orderItemRepository.findAll(any(Specification.class), eq(expectedRequest)))
                .thenReturn(Page.empty());

        // when
        PageResponse<OrderItemDto> response = orderItemService.getOrderItems(criteria, pageNo, pageSize);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).isEmpty();
        verify(orderItemRepository).findAll(any(Specification.class), eq(expectedRequest));
    }

    @Test
    void getOrderItems_AllCriteriaNonNull_ReturnsExpectedPage() {
        // given
        // All fields are non-null => combine all specs
        OrderItemSearchCriteria criteria = new OrderItemSearchCriteria(1, 100, 1, 100, 90.99);
        int pageNo = 0;
        int pageSize = 3;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        Page<OrderItemEntity> mockPage =
                new PageImpl<>(List.of(orderItemEntity), pageRequest, 1);

        when(orderItemRepository.findAll(any(Specification.class), eq(pageRequest)))
                .thenReturn(mockPage);

        // when
        PageResponse<OrderItemDto> response = orderItemService.getOrderItems(criteria, pageNo, pageSize);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().getFirst().getId()).isEqualTo(1);
        verify(orderItemRepository).findAll(any(Specification.class), eq(pageRequest));
    }

    // ----------------------------------------------------------------------------------------
    // getOrderItemById
    // ----------------------------------------------------------------------------------------
    @Test
    void getOrderItemById_ItemExists_ReturnsDto() {
        // given
        when(orderItemRepository.findById(1)).thenReturn(Optional.of(orderItemEntity));

        // when
        OrderItemDto actual = orderItemService.getOrderItemById(1);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(1);
        verify(orderItemRepository).findById(1);
    }

    @Test
    void getOrderItemById_ItemNotFound_ThrowsResourceNotFoundException() {
        // given
        when(orderItemRepository.findById(999)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> orderItemService.getOrderItemById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order item with id 999 not found");
        verify(orderItemRepository).findById(999);
    }

    // ----------------------------------------------------------------------------------------
    // createOrderItem
    // ----------------------------------------------------------------------------------------
    @Test
    void createOrderItem_ValidData_CreatesSuccessfully() {
        // given
        when(orderItemRepository.save(any(OrderItemEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        OrderItemDto createdItem = orderItemService.createOrderItem(orderItemDto);

        // then
        assertThat(createdItem).isNotNull();
        assertThat(createdItem.getId()).isEqualTo(1);
        verify(orderItemRepository).save(orderItemEntity);
    }

    @Test
    void createOrderItem_ExceptionThrown_ThrowsObjectCreationException() {
        // given
        when(orderItemRepository.save(any(OrderItemEntity.class))).thenThrow(new RuntimeException("DB error"));

        // when / then
        assertThatThrownBy(() -> orderItemService.createOrderItem(orderItemDto))
                .isInstanceOf(ObjectCreationException.class)
                .hasMessageContaining("Failed to create order item: DB error");
        verify(orderItemRepository).save(any(OrderItemEntity.class));
    }

    // ----------------------------------------------------------------------------------------
    // deleteOrderItem
    // ----------------------------------------------------------------------------------------
    @Test
    void deleteOrderItem_ItemExists_DeletesSuccessfully() {
        // given
        when(orderItemRepository.findById(10)).thenReturn(Optional.of(orderItemEntity));

        // when
        orderItemService.deleteOrderItem(10);

        // then
        verify(orderItemRepository).findById(10);
        verify(orderItemRepository).delete(orderItemEntity);
    }

    @Test
    void deleteOrderItem_ItemNotFound_ThrowsResourceNotFoundException() {
        // given
        when(orderItemRepository.findById(999)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> orderItemService.deleteOrderItem(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order item with id 999 not found");
        verify(orderItemRepository).findById(999);
        verify(orderItemRepository, never()).delete(any(OrderItemEntity.class));
    }
}
