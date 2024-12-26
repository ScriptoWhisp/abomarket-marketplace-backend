package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.criteria.OrderItemSearchCriteria;
import ee.taltech.iti03022024project.domain.OrderItemEntity;
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

    @BeforeEach
    void setUp() {
        orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderItemId(10);

        orderItemDto = OrderItemDto.builder().id(10).build();

        lenient().when(orderItemMapper.toEntity(any(OrderItemDto.class))).thenReturn(orderItemEntity);
        lenient().when(orderItemMapper.toDto(any(OrderItemEntity.class))).thenReturn(orderItemDto);
    }

    // ----------------------------------------------------------------------------------------
    // getOrderItems
    // ----------------------------------------------------------------------------------------
    @Test
    void getOrderItems_ValidCriteria_ReturnsPagedOrderItems() {
        // given
        OrderItemSearchCriteria criteria = OrderItemSearchCriteria.builder().id(10).build();
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
        assertThat(response.content().getFirst().getId()).isEqualTo(10);
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
    void getOrderItems_AllCriteriaNull_UsesEmptySpec() {
        // given
        // All criteria are null => no spec filters
        OrderItemSearchCriteria criteria = new OrderItemSearchCriteria(null, null, null, null, null);
        int pageNo = 0;
        int pageSize = 5;

        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<OrderItemEntity> mockPage =
                new PageImpl<>(Collections.singletonList(orderItemEntity), pageRequest, 1);

        when(orderItemRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(mockPage);

        // when
        PageResponse<OrderItemDto> response = orderItemService.getOrderItems(criteria, pageNo, pageSize);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).getId()).isEqualTo(10);
        verify(orderItemRepository).findAll(any(Specification.class), eq(pageRequest));
    }

    @Test
    void getOrderItems_IdCriteria_AddsSpec() {
        // given
        // Only ID is non-null
        OrderItemSearchCriteria criteria = new OrderItemSearchCriteria(10, null, null, null, null);
        int pageNo = 0;
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        when(orderItemRepository.findAll(any(Specification.class), eq(pageRequest)))
                .thenReturn(Page.empty());

        // when
        PageResponse<OrderItemDto> response = orderItemService.getOrderItems(criteria, pageNo, pageSize);

        // then
        assertThat(response).isNotNull();
        // we expect an empty page, but crucially we tested the ID branch
        assertThat(response.content()).isEmpty();
        verify(orderItemRepository).findAll(any(Specification.class), eq(pageRequest));
    }

    @Test
    void getOrderItems_QuantityCriteria_AddsSpec() {
        // given
        // Only quantity is non-null
        OrderItemSearchCriteria criteria = new OrderItemSearchCriteria(null, 5, null, null, null);
        int pageNo = 0;
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        when(orderItemRepository.findAll(any(Specification.class), eq(pageRequest)))
                .thenReturn(Page.empty());

        // when
        PageResponse<OrderItemDto> response = orderItemService.getOrderItems(criteria, pageNo, pageSize);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).isEmpty();
        verify(orderItemRepository).findAll(any(Specification.class), eq(pageRequest));
    }

    @Test
    void getOrderItems_ProductIdCriteria_AddsSpec() {
        // given
        OrderItemSearchCriteria criteria = new OrderItemSearchCriteria(null, null, 20, null, null);
        int pageNo = 0;
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        when(orderItemRepository.findAll(any(Specification.class), eq(pageRequest)))
                .thenReturn(Page.empty());

        // when
        PageResponse<OrderItemDto> response = orderItemService.getOrderItems(criteria, pageNo, pageSize);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).isEmpty();
        verify(orderItemRepository).findAll(any(Specification.class), eq(pageRequest));
    }

    @Test
    void getOrderItems_OrderIdCriteria_AddsSpec() {
        // given
        OrderItemSearchCriteria criteria = new OrderItemSearchCriteria(null, null, null, 100, null);
        int pageNo = 0;
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        when(orderItemRepository.findAll(any(Specification.class), eq(pageRequest)))
                .thenReturn(Page.empty());

        // when
        PageResponse<OrderItemDto> response = orderItemService.getOrderItems(criteria, pageNo, pageSize);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).isEmpty();
        verify(orderItemRepository).findAll(any(Specification.class), eq(pageRequest));
    }

    @Test
    void getOrderItems_PriceAtTimeOfOrderCriteria_AddsSpec() {
        // given
        OrderItemSearchCriteria criteria = new OrderItemSearchCriteria(null, null, null, null, 9.99);
        int pageNo = 0;
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        when(orderItemRepository.findAll(any(Specification.class), eq(pageRequest)))
                .thenReturn(Page.empty());

        // when
        PageResponse<OrderItemDto> response = orderItemService.getOrderItems(criteria, pageNo, pageSize);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).isEmpty();
        verify(orderItemRepository).findAll(any(Specification.class), eq(pageRequest));
    }

    @Test
    void getOrderItems_NegativePageNo_DefaultsToZero() {
        // given
        // negative pageNo => should default to 0
        OrderItemSearchCriteria criteria = new OrderItemSearchCriteria(null, null, null, null, null);
        int pageNo = -1;  // negative
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
        // pageSize < 1 => defaults to 1
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
        OrderItemSearchCriteria criteria = new OrderItemSearchCriteria(10, 5, 20, 100, 9.99);
        int pageNo = 2;
        int pageSize = 3;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        Page<OrderItemEntity> mockPage =
                new PageImpl<>(Collections.singletonList(orderItemEntity), pageRequest, 1);

        when(orderItemRepository.findAll(any(Specification.class), eq(pageRequest)))
                .thenReturn(mockPage);

        // when
        PageResponse<OrderItemDto> response = orderItemService.getOrderItems(criteria, pageNo, pageSize);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().getFirst().getId()).isEqualTo(10);
        verify(orderItemRepository).findAll(any(Specification.class), eq(pageRequest));
    }

    // ----------------------------------------------------------------------------------------
    // getOrderItemById
    // ----------------------------------------------------------------------------------------
    @Test
    void getOrderItemById_ItemExists_ReturnsDto() {
        // given
        when(orderItemRepository.findById(10)).thenReturn(Optional.of(orderItemEntity));

        // when
        OrderItemDto actual = orderItemService.getOrderItemById(10);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(10);
        verify(orderItemRepository).findById(10);
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
        assertThat(createdItem.getId()).isEqualTo(10);
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
