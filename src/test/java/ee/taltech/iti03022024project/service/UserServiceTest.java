package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.domain.OrderEntity;
import ee.taltech.iti03022024project.domain.RoleEntity;
import ee.taltech.iti03022024project.domain.UserEntity;
import ee.taltech.iti03022024project.dto.UserDto;
import ee.taltech.iti03022024project.exception.BadTokenException;
import ee.taltech.iti03022024project.exception.ResourceNotFoundException;
import ee.taltech.iti03022024project.mapstruct.UserMapper;
import ee.taltech.iti03022024project.repository.RolesRepository;
import ee.taltech.iti03022024project.repository.UsersRepository;
import ee.taltech.iti03022024project.responses.PageResponse;
import ee.taltech.iti03022024project.security.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private RolesRepository rolesRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private OrderService orderService;
    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserEntity userEntity;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setUserId(100);
        userEntity.setEmail("test@example.com");
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");

        userDto = UserDto.builder()
                .id(100)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    // ---------------------------------------------------------------------------------------------
    // getUsers
    // ---------------------------------------------------------------------------------------------
    @Test
    void getUsers_ValidSearch_ReturnsPageOfUsers() {
        // given
        String search = "example";
        int pageNo = 0;
        int pageSize = 10;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        List<UserEntity> userEntityList = Collections.singletonList(userEntity);
        Page<UserEntity> userPage = new PageImpl<>(userEntityList, pageRequest, userEntityList.size());

        when(usersRepository.findAllByEmailContaining(eq(search), any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        // when
        PageResponse<UserDto> response = userService.getUsers(search, pageNo, pageSize);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().getFirst().getEmail()).isEqualTo("test@example.com");
        verify(usersRepository).findAllByEmailContaining(eq(search), any(Pageable.class));
    }

    @Test
    void getUsers_NotMatchingSearch_ReturnsEmptyPage() {
        // given
        String search = "examples";
        int pageNo = 0;
        int pageSize = 10;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        Page<UserEntity> userPage = new PageImpl<>(List.of(), pageRequest, 0);

        when(usersRepository.findAllByEmailContaining(eq(search), any(Pageable.class))).thenReturn(userPage);

        // when
        PageResponse<UserDto> response = userService.getUsers(search, pageNo, pageSize);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).isEmpty();
        verify(usersRepository).findAllByEmailContaining(eq(search), any(Pageable.class));
    }

    // ---------------------------------------------------------------------------------------------
    // getUserById
    // ---------------------------------------------------------------------------------------------
    @Test
    void getUserById_UserExists_ReturnsUser() {
        // given
        int userId = 100;
        when(usersRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        // when
        UserDto actual = userService.getUserById(userId);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getEmail()).isEqualTo("test@example.com");
        verify(usersRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_UserDoesNotExist_ThrowsResourceNotFoundException() {
        // given
        int userId = 999;
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with id 999 not found");
        verify(usersRepository, times(1)).findById(userId);
    }

    // ---------------------------------------------------------------------------------------------
    // getAuthorizedUser
    // ---------------------------------------------------------------------------------------------
    @Test
    void getAuthorizedUser_AuthorizedUserExists_ReturnsUser() {
        // given
        when(authenticationFacade.getAuthenticatedUser()).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        // when
        UserDto actual = userService.getAuthorizedUser();

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getEmail()).isEqualTo("test@example.com");
        verify(authenticationFacade, times(1)).getAuthenticatedUser();
    }

    @Test
    void getAuthorizedUser_AuthenticationIsNull_ThrowsBadTokenException() {
        // given
        when(authenticationFacade.getAuthenticatedUser()).thenReturn(null);

        // when / then
        assertThatThrownBy(() -> userService.getAuthorizedUser())
                .isInstanceOf(BadTokenException.class)
                .hasMessageContaining("User not authorized");
        verify(authenticationFacade, times(1)).getAuthenticatedUser();
    }

    // ---------------------------------------------------------------------------------------------
    // patchedAuthorizedUser
    // ---------------------------------------------------------------------------------------------
    @Test
    void patchAuthorizedUser_UserIsAuthorized_UpdatesAndReturnsUser() {
        // given
        when(authenticationFacade.getAuthenticatedUser()).thenReturn(userEntity);
        when(userMapper.toDto(any(UserEntity.class))).thenReturn(userDto);
        when(usersRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userDto.setFirstName("NewName");

        // when
        UserDto patchedUser = userService.patchAuthorizedUser(userDto);

        // then
        assertThat(patchedUser).isNotNull();
        assertThat(patchedUser.getFirstName()).isEqualTo("NewName");
        verify(authenticationFacade, times(1)).getAuthenticatedUser();
        verify(usersRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void patchAuthorizedUser_AuthenticationIsNull_ThrowsBadTokenException() {
        // given
        when(authenticationFacade.getAuthenticatedUser()).thenReturn(null);

        // when / then
        assertThatThrownBy(() -> userService.patchAuthorizedUser(userDto))
                .isInstanceOf(BadTokenException.class)
                .hasMessageContaining("User not authorized");
        verify(authenticationFacade, times(1)).getAuthenticatedUser();
        verify(usersRepository, never()).save(any(UserEntity.class));
    }

    // ---------------------------------------------------------------------------------------------
    // patchedUserById
    // ---------------------------------------------------------------------------------------------
    @Test
    void patchUserById_UserExists_UpdatesAndReturnsUser() {
        // given
        int userId = 100;
        when(usersRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(any(UserEntity.class))).thenReturn(userDto);
        when(usersRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userDto.setEmail("newemail@example.com");

        // when
        UserDto updatedDto = userService.patchUserById(userId, userDto);

        // then
        assertThat(updatedDto).isNotNull();
        assertThat(updatedDto.getEmail()).isEqualTo("newemail@example.com");
        verify(usersRepository, times(1)).findById(userId);
        verify(usersRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void patchUserById_UserDoesNotExist_ThrowsResourceNotFoundException() {
        // given
        int userId = 999;
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.patchUserById(userId, userDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with id 999 not found");
        verify(usersRepository, times(1)).findById(userId);
        verify(usersRepository, never()).save(any(UserEntity.class));
    }

    // ---------------------------------------------------------------------------------------------
    // updateUser
    // ---------------------------------------------------------------------------------------------
    @Test
    void updateUser_AllFieldsProvided_UpdatesAllFields() {
        // given
        userDto.setFirstName("NewFirstName");
        userDto.setLastName("NewLastName");
        userDto.setEmail("NewEmail");
        userDto.setPassword("NewPassword");
        userDto.setPhone("NewPhone");
        userDto.setLocation("NewLocation");

        when(usersRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        // when
        invokeUpdateUser();

        // then
        assertThat(userEntity.getFirstName()).isEqualTo("NewFirstName");
        assertThat(userEntity.getLastName()).isEqualTo("NewLastName");
        assertThat(userEntity.getEmail()).isEqualTo("NewEmail");
        assertThat(userEntity.getPassword()).isEqualTo("NewPassword");
        assertThat(userEntity.getPhone()).isEqualTo("NewPhone");
        assertThat(userEntity.getLocation()).isEqualTo("NewLocation");
        verify(usersRepository).save(userEntity);
    }

    @Test
    void updateUser_SomeFieldsProvided_UpdatesOnlyProvidedFields() {
        // given
        userDto.setFirstName("NewFirstName");
        userDto.setEmail("NewEmail");

        when(usersRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        // when
        invokeUpdateUser();

        // then
        assertThat(userEntity.getFirstName()).isEqualTo("NewFirstName");
        assertThat(userEntity.getLastName()).isEqualTo("Doe");
        assertThat(userEntity.getEmail()).isEqualTo("NewEmail");
        assertThat(userEntity.getPassword()).isNull();
        assertThat(userEntity.getPhone()).isNull();
        assertThat(userEntity.getLocation()).isNull();
        verify(usersRepository).save(userEntity);
    }

    @Test
    void updateUser_FirstNameNotProvided_UpdatesFirstName() {
        // given
        userEntity.setFirstName("FirstName");
        userDto.setFirstName(null); // other fields remain unchanged
        when(usersRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        // when
        invokeUpdateUser();

        // then
        assertThat(userEntity.getFirstName()).isEqualTo("FirstName");
        verify(usersRepository).save(userEntity);
    }

    @Test
    void updateUser_LastNameNotProvided_DoesNotUpdateLastName() {
        // given
        userEntity.setLastName("OriginalLastName");
        userDto.setLastName(null); // ensure no update for last name
        when(usersRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        // when
        invokeUpdateUser();

        // then
        assertThat(userEntity.getLastName()).isEqualTo("OriginalLastName");
        verify(usersRepository).save(userEntity);
    }

    @Test
    void updateUser_EmailNotProvided_DoesNotUpdateEmail() {
        // given
        userEntity.setEmail("original.email@example.com");
        userDto.setEmail(null); // no update for email
        when(usersRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        // when
        invokeUpdateUser();

        // then
        assertThat(userEntity.getEmail()).isEqualTo("original.email@example.com");
        verify(usersRepository).save(userEntity);
    }

    @Test
    void updateUser_PasswordNotProvided_DoesNotUpdatePassword() {
        // given
        userEntity.setPassword("originalPassword");
        userDto.setPassword(null); // no update for password
        when(usersRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        // when
        invokeUpdateUser();

        // then
        assertThat(userEntity.getPassword()).isEqualTo("originalPassword");
        verify(usersRepository).save(userEntity);
    }


    @Test
    void updateUser_PhoneNotProvided_DoesNotUpdatePhone() {
        // given
        userEntity.setPhone("987654321");
        userDto.setPhone(null); // no update for phone
        when(usersRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        // when
        invokeUpdateUser();

        // then
        assertThat(userEntity.getPhone()).isEqualTo("987654321");
        verify(usersRepository).save(userEntity);
    }

    @Test
    void updateUser_LocationNotProvided_DoesNotUpdateLocation() {
        // given
        userEntity.setLocation("OriginalLocation");
        userDto.setLocation(null); // no update for location
        when(usersRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        // when
        invokeUpdateUser();

        // then
        assertThat(userEntity.getLocation()).isEqualTo("OriginalLocation");
        verify(usersRepository).save(userEntity);
    }

    private void invokeUpdateUser() {
        when(usersRepository.findById(100)).thenReturn(Optional.of(userEntity));
        userService.patchUserById(userEntity.getUserId(), userDto);
    }

    // ---------------------------------------------------------------------------------------------
    // createUser
    // ---------------------------------------------------------------------------------------------
    @Test
    void createUser_HappyPath_CreatesUser() {
        // given
        RoleEntity defaultRole = new RoleEntity();
        defaultRole.setRoleId(1);
        defaultRole.setRoleName("USER");

        OrderEntity unfinishedOrder = new OrderEntity();
        unfinishedOrder.setOrderId(200);

        when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userMapper.toEntity(userDto)).thenReturn(userEntity);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encoded_pass");
        when(rolesRepository.getReferenceById(1)).thenReturn(defaultRole);
        when(usersRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // simulate DB assign
        when(orderService.createUnfinishedOrderForUser(any(UserEntity.class))).thenReturn(unfinishedOrder);
        when(userMapper.toDto(any(UserEntity.class))).thenReturn(userDto);

        // when
        UserDto createdUser = userService.createUser(userDto);

        // then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo("test@example.com");

        verify(usersRepository, times(1)).findByEmail("test@example.com");
        verify(usersRepository, times(2)).save(any(UserEntity.class)); // called twice
        verify(rolesRepository, times(1)).getReferenceById(1);
        verify(orderService, times(1)).createUnfinishedOrderForUser(any(UserEntity.class));
    }

    @Test
    void createUser_UserAlreadyExists_ThrowsBadTokenException() {
        // given
        when(usersRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(userEntity)); // already in DB

        // when / then
        assertThatThrownBy(() -> userService.createUser(userDto))
                .isInstanceOf(BadTokenException.class)
                .hasMessageContaining("Failed to create user: User with email test@example.com already exists");

        verify(usersRepository, times(1)).findByEmail("test@example.com");
        verify(usersRepository, never()).save(any(UserEntity.class));
        verify(orderService, never()).createUnfinishedOrderForUser(any(UserEntity.class));
    }

    @Test
    void createUser_CreatingUser_DefaultRoleIsAssigned() {
        // given
        RoleEntity defaultRole = new RoleEntity();
        defaultRole.setRoleId(1);
        defaultRole.setRoleName("USER");

        when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userMapper.toEntity(userDto)).thenReturn(userEntity);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(rolesRepository.getReferenceById(1)).thenReturn(defaultRole);
        when(usersRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderService.createUnfinishedOrderForUser(any(UserEntity.class))).thenReturn(new OrderEntity());
        when(userMapper.toDto(any(UserEntity.class))).thenReturn(userDto);

        // when
        UserDto createdUser = userService.createUser(userDto);

        // then
        assertThat(createdUser).isNotNull();
        assertThat(userEntity.getRole()).isNotNull();
        assertThat(userEntity.getRole().getRoleId()).isEqualTo(1);
        assertThat(userEntity.getRole().getRoleName()).isEqualTo("USER");
        verify(rolesRepository, times(1)).getReferenceById(1);
        verify(usersRepository, times(2)).save(any(UserEntity.class)); // Initial save and after unfinished order assignment
    }

    @Test
    void createUser_CreateUser_UnfinishedOrderIsCreatedAndSet() {
        // given
        RoleEntity defaultRole = new RoleEntity();
        defaultRole.setRoleId(1);
        defaultRole.setRoleName("USER");

        OrderEntity unfinishedOrder = new OrderEntity();
        unfinishedOrder.setOrderId(200);

        when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userMapper.toEntity(userDto)).thenReturn(userEntity);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(rolesRepository.getReferenceById(1)).thenReturn(defaultRole);
        when(usersRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Simulate save behavior
        when(orderService.createUnfinishedOrderForUser(any(UserEntity.class))).thenReturn(unfinishedOrder);
        when(userMapper.toDto(any(UserEntity.class))).thenReturn(userDto);

        // when
        UserDto createdUser = userService.createUser(userDto);

        // then
        assertThat(createdUser).isNotNull();
        assertThat(userEntity.getUnfinishedOrder()).isNotNull();
        assertThat(userEntity.getUnfinishedOrder().getOrderId()).isEqualTo(200);
        verify(orderService, times(1)).createUnfinishedOrderForUser(userEntity);
        verify(usersRepository, times(2)).save(userEntity); // First save before order creation, second save after setting order
    }

    // ---------------------------------------------------------------------------------------------
    // deleteUser
    // ---------------------------------------------------------------------------------------------
    @Test
    void deleteUser_UserExists_DeletesUser() {
        // given
        int userId = 100;
        when(usersRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        // when
        userService.deleteUser(userId);

        // then
        verify(usersRepository, times(1)).findById(userId);
        verify(usersRepository, times(1)).delete(userEntity);
    }

    @Test
    void deleteUser_UserDoesNotExist_ThrowsResourceNotFoundException() {
        // given
        int userId = 999;
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with id 999 not found");
        verify(usersRepository, times(1)).findById(userId);
        verify(usersRepository, never()).delete(any(UserEntity.class));
    }
}
