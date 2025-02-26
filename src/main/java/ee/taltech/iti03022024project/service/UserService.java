package ee.taltech.iti03022024project.service;


import ee.taltech.iti03022024project.domain.OrderEntity;
import ee.taltech.iti03022024project.domain.RoleEntity;
import ee.taltech.iti03022024project.domain.UserEntity;
import ee.taltech.iti03022024project.dto.UserDto;
import ee.taltech.iti03022024project.exception.BadTokenException;
import ee.taltech.iti03022024project.exception.ObjectCreationException;
import ee.taltech.iti03022024project.exception.ResourceNotFoundException;
import ee.taltech.iti03022024project.mapstruct.UserMapper;
import ee.taltech.iti03022024project.repository.RolesRepository;
import ee.taltech.iti03022024project.repository.UsersRepository;
import ee.taltech.iti03022024project.responses.PageResponse;
import ee.taltech.iti03022024project.security.AuthenticationFacade;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final UserMapper userMapper;

    private final OrderService orderService;

    private final AuthenticationFacade authenticationFacade;
    private final PasswordEncoder passwordEncoder;

    private static final String NOT_FOUND_MSG = "User with id %s not found";

    public PageResponse<UserDto> getUsers(String search, int pageNo, int pageSize) {
        log.info("Attempting to get users with search: {}, page: {}, size: {}", search, pageNo, pageSize);
        Pageable paging = PageRequest.of(pageNo, pageSize);
        Page<UserEntity> users = usersRepository.findAllByEmailContaining(search, paging);
        return new PageResponse<>(users.map(userMapper::toDto));
    }

    public UserDto getUserById(int id) {
        return usersRepository.findById(id).map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG.formatted(id)));
    }

    public UserDto getAuthorizedUser() {
        UserEntity userEntity = authenticationFacade.getAuthenticatedUser();
        if (userEntity == null)
            throw new BadTokenException("User not authorized");
        else {
            return userMapper.toDto(userEntity);
        }
    }

    public UserDto patchAuthorizedUser(UserDto userDto) {
        log.info("Checking if user is authorized");
        UserEntity authorizedUser = authenticationFacade.getAuthenticatedUser();
        if (authorizedUser == null)
            throw new BadTokenException("User not authorized");
        else {
            log.info("User authorized: {}", authorizedUser);
            return updateUser(authorizedUser, userDto);
        }
    }

    public UserDto patchUserById(int id, UserDto userDto) {
        UserEntity userToUpdate = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG.formatted(id)));
        return updateUser(userToUpdate, userDto);
    }

    private UserDto updateUser(UserEntity userToUpdate, UserDto userDto) {
        log.info("Attempting to update user with id {}, with data:{}", userToUpdate.getUserId(), userDto);
        userToUpdate.setFirstName(userDto.getFirstName() != null ? userDto.getFirstName() : userToUpdate.getFirstName());
        userToUpdate.setLastName(userDto.getLastName() != null ? userDto.getLastName() : userToUpdate.getLastName());
        userToUpdate.setEmail(userDto.getEmail() != null ? userDto.getEmail() : userToUpdate.getEmail());
        userToUpdate.setPassword(userDto.getPassword() != null ? userDto.getPassword() : userToUpdate.getPassword());
        userToUpdate.setPhone(userDto.getPhone() != null ? userDto.getPhone() : userToUpdate.getPhone());
        userToUpdate.setLocation(userDto.getLocation() != null ? userDto.getLocation() : userToUpdate.getLocation());
        userToUpdate.setUnfinishedOrder(userDto.getUnfinishedOrderId() != null ? userMapper.mapUnfinishedOrderIdToOrderEntity(userDto.getUnfinishedOrderId()) : userToUpdate.getUnfinishedOrder());
        usersRepository.save(userToUpdate);
        log.info("User updated successfully: {}", userToUpdate);
        return userMapper.toDto(userToUpdate);
    }

    public UserDto createUser(UserDto userDto) {
        log.info("Attempting to create user with data: {}", userDto);
        if (usersRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ObjectCreationException("User with email " + userDto.getEmail() + " already exists");
        }
        UserEntity newUser = userMapper.toEntity(userDto);
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));

        RoleEntity defaultRole = rolesRepository.getReferenceById(1);
        newUser.setRole(defaultRole); // set default role

        // save user to give him unique id
        UserEntity newUserWithId = usersRepository.save(newUser);

        // create a cart for user
        OrderEntity userUnfinishedOrder = orderService.createUnfinishedOrderForUser(newUserWithId);
        newUserWithId.setUnfinishedOrder(userUnfinishedOrder);

        UserEntity savedUser = usersRepository.save(newUserWithId);
        log.info("User created successfully: {}", savedUser);
        return userMapper.toDto(savedUser);
    }

    public void deleteUser(int id) {
        log.info("Attempting to delete user with id {}", id);
        UserEntity userToDelete = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG.formatted(id)));
        usersRepository.delete(userToDelete);
        log.info("User deleted successfully: {}", userToDelete);
    }
}
