package ee.taltech.iti03022024project.service;


import ee.taltech.iti03022024project.domain.UserEntity;
import ee.taltech.iti03022024project.dto.UserDto;
import ee.taltech.iti03022024project.exception.BadTokenException;
import ee.taltech.iti03022024project.exception.ResourceNotFoundException;
import ee.taltech.iti03022024project.mapstruct.UserMapper;
import ee.taltech.iti03022024project.repository.UsersRepository;
import ee.taltech.iti03022024project.security.AuthenticationFacade;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;

    private final AuthenticationFacade authenticationFacade;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> getUsers() {
        return usersRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    public UserDto getUserById(int id) {
        return usersRepository.findById(id).map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    public UserDto getAuthorizedUser() {
        if (authenticationFacade.getAuthenticatedUser() == null)
            throw new BadTokenException("User not authorized");
        else {
            UserEntity userEntity = authenticationFacade.getAuthenticatedUser();
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

    private UserDto updateUser(UserEntity userToUpdate, UserDto userDto) {
        log.info("Attempting to update user with id {}, with data:{}", userToUpdate.getUserId(), userDto);
        userToUpdate.setFirstName(userDto.getFirstName() != null ? userDto.getFirstName() : userToUpdate.getFirstName());
        userToUpdate.setLastName(userDto.getLastName() != null ? userDto.getLastName() : userToUpdate.getLastName());
        userToUpdate.setEmail(userDto.getEmail() != null ? userDto.getEmail() : userToUpdate.getEmail());
        userToUpdate.setPassword(userDto.getPassword() != null ? userDto.getPassword() : userToUpdate.getPassword());
        userToUpdate.setPhone(userDto.getPhone() != null ? userDto.getPhone() : userToUpdate.getPhone());
        userToUpdate.setLocation(userDto.getLocation() != null ? userDto.getLocation() : userToUpdate.getLocation());
        usersRepository.save(userToUpdate);
        log.info("User updated successfully: {}", userToUpdate);
        return userMapper.toDto(userToUpdate);
    }

    public UserDto createUser(UserDto userDto) {
        try {
            log.info("Attempting to create user with data: {}", userDto);
            UserEntity newUser = userMapper.toEntity(userDto);
            newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
            UserEntity savedUser = usersRepository.save(newUser);
            log.info("User created successfully: {}", newUser);
            return userMapper.toDto(savedUser);
        } catch (Exception e) {
            throw new BadTokenException("Failed to create user: " + e.getMessage());
        }
    }

    public void deleteUser(int id) {
        log.info("Attempting to delete user with id {}", id);
        UserEntity userToDelete = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        usersRepository.delete(userToDelete);
        log.info("User deleted successfully: {}", userToDelete);
    }
}
