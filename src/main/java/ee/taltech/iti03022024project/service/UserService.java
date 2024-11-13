package ee.taltech.iti03022024project.service;


import ee.taltech.iti03022024project.domain.UserEntity;
import ee.taltech.iti03022024project.dto.UserDto;
import ee.taltech.iti03022024project.mapstruct.UserMapper;
import ee.taltech.iti03022024project.repository.UsersRepository;
import ee.taltech.iti03022024project.security.AuthenticationFacade;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<UserDto> getUserById(int id) {
        return usersRepository.findById(id).map(userMapper::toDto);
    }

    public Optional<UserDto> getAuthorizedUser() {
        return Optional.ofNullable(authenticationFacade.getAuthenticatedUser())
                .map(userMapper::toDto);
    }

    public Optional<UserDto> patchAuthorizedUser(UserDto userDto) {
        return updateUser(authenticationFacade.getAuthenticatedUser(), userDto);
    }

    private Optional<UserDto> updateUser(UserEntity userToUpdate, UserDto userDto) {
        userToUpdate.setFirstName(userDto.getFirstName() != null ? userDto.getFirstName() : userToUpdate.getFirstName());
        userToUpdate.setLastName(userDto.getLastName() != null ? userDto.getLastName() : userToUpdate.getLastName());
        userToUpdate.setEmail(userDto.getEmail() != null ? userDto.getEmail() : userToUpdate.getEmail());
        userToUpdate.setPassword(userDto.getPassword() != null ? userDto.getPassword() : userToUpdate.getPassword());
        userToUpdate.setPhone(userDto.getPhone() != null ? userDto.getPhone() : userToUpdate.getPhone());
        userToUpdate.setLocation(userDto.getLocation() != null ? userDto.getLocation() : userToUpdate.getLocation());
        usersRepository.save(userToUpdate);
        return Optional.of(userMapper.toDto(userToUpdate));
    }

    public Optional<UserDto> createUser(UserDto userDto) {
        UserEntity newUser = userMapper.toEntity(userDto);
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        UserEntity savedUser = usersRepository.save(newUser);
        return Optional.of(userMapper.toDto(savedUser));
    }

    public Optional<UserDto> deleteUser(int id) {
        Optional<UserEntity> userToDelete = usersRepository.findById(id);
        userToDelete.ifPresent(usersRepository::delete);
        return userToDelete.map(userMapper::toDto);
    }

}
