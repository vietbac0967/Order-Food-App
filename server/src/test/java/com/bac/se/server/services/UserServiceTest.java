package com.bac.se.server.services;

import com.bac.se.server.dto.requests.RegisterRequest;
import com.bac.se.server.dto.responses.UserResponse;
import com.bac.se.server.enums.Role;
import com.bac.se.server.exceptions.AlreadyExistException;
import com.bac.se.server.exceptions.NotFoundException;
import com.bac.se.server.models.User;
import com.bac.se.server.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void canGetAllUsers() {
        userService.getUsers();
        verify(userRepository).findAll();
    }

    @Test
    void mapToUser() {
        User user = User.builder()
                .email("hai@gmail.com")
                .password("123")
                .role(Role.USER)
                .name("Hai Anh")
                .address("HCM")
                .phone("081681251")
                .build();
        UserResponse userResponse = userService.mapToUser(user);
        assertEquals(user.getPhone(), userResponse.phone());
        assertEquals(user.getEmail(), userResponse.email());
        assertEquals(user.getAddress(), userResponse.address());
    }

    @Test
    void canGetUserByIdSuccess() {
        Long userId = 1L;
        User expected = User.builder()
                .id(userId)
                .name("John Doe")
                .email("doe@gmail.com")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expected));
        User actualUser = userService.getUserById(userId);
        assertEquals(expected, actualUser);
    }

    @Test
    void canGetUserByIdNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));

    }

    @Test
    void deleteUser() {
    }

    @Test
    void canGetUserByEmail() {
        String email = "a@gmail.com";
        User expected = User.builder()
                .id(1L)
                .name("John Doe")
                .email(email)
                .build();
        when(userRepository.getUserByEmail(email)).thenReturn(Optional.of(expected));
        User actualUser = userService.getUserByEmail(email);
        assertEquals(expected, actualUser);
    }

    @Test
    void canRegisterAccountSuccess() {
        RegisterRequest userRequest = new RegisterRequest("john", "john@example.com", "1234567890", "123 Main St", "password");
        assertNotEquals(userRequest.password(),encoder.encode(userRequest.password()));
        when(userRepository.getUserByEmail(userRequest.email())).thenReturn(Optional.empty());
        when(userRepository.getUserByPhone(userRequest.phone())).thenReturn(Optional.empty());
        User user = User.builder()
                .name(userRequest.username())
                .address(userRequest.address())
                .phone(userRequest.phone())
                .email(userRequest.email())
                .createdAt(new Date())
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);
        // Act
        UserResponse response = userService.createUser(userRequest);

        // Assert
        assertEquals(user.getName(), response.name());
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getAddress(), response.address());
        assertEquals(user.getPhone(), response.phone());

        verify(userRepository).getUserByEmail(userRequest.email());
        verify(userRepository).getUserByPhone(userRequest.phone());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void  createUserEmailAlreadyExists(){
//        RegisterRequest userRequest = new RegisterRequest("john", "john@example.com", "1234567890", "123 Main St", "password");
//        when(userRepository.getUserByEmail(userRequest.email())).thenReturn(Optional.of(new User()));
//
//        // Act & Assert
//        AlreadyExistException exception = assertThrows(AlreadyExistException.class, () -> userService.createUser(userRequest));
//        assertEquals("Email is already exist", exception.getMessage());
//
//        verify(userRepository).getUserByEmail(userRequest.email());
//        verify(userRepository, never()).getUserByPhone(anyString());
//        verify(userRepository, never()).save(any(User.class));
    }
}