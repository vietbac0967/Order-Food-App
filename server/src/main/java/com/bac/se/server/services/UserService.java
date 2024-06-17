package com.bac.se.server.services;

import com.bac.se.server.dto.requests.RegisterRequest;
import com.bac.se.server.dto.responses.LoginResponse;
import com.bac.se.server.dto.responses.UserResponse;
import com.bac.se.server.enums.Role;
import com.bac.se.server.exceptions.AlreadyExistException;
import com.bac.se.server.exceptions.TokenRefreshException;
import com.bac.se.server.exceptions.NotFoundException;
import com.bac.se.server.models.User;
import com.bac.se.server.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private RedisTemplate<String, String> template;
    @Autowired
    private JWTService jwtService;

    public static final String ACCESS_SECRET_KEY = System.getenv("JWT_ACCESS_TOKEN");
    public static final String REFRESH_SECRET_KEY = System.getenv("JWT_REFRESH_TOKEN");


    @Override
    public UserDetails loadUserByUsername(String username)  {
        final var userByEmail = userRepository.getUserByEmail(username);
        return userByEmail.map(UserInfoDetail::new)
                .orElseThrow(() -> new NotFoundException("User not found"));

    }

    public UserResponse createUser(RegisterRequest userRequest) {
        final var userByEmail = userRepository.getUserByEmail(userRequest.email());
        final var userByPhone = userRepository.getUserByPhone(userRequest.phone());
        if (userByEmail.isPresent()) {
            throw new AlreadyExistException("Email is already exist");
        }
        if (userByPhone.isPresent()) {
            throw new AlreadyExistException("Phone is already exist");
        }
        User user = User.builder()
                .name(userRequest.username())
                .address(userRequest.address())
                .phone(userRequest.phone())
                .email(userRequest.email())
                .createdAt(new Date())
                .password(encoder.encode(userRequest.password()))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        log.info("User {} is saved", user.getId());
        return new UserResponse(user.getName(), user.getEmail(), user.getAddress(), user.getPhone());
    }

    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(this::mapToUser)
                .toList();
    }


    public UserResponse mapToUser(User user) {
        return new UserResponse(user.getName(), user.getEmail(), user.getAddress(), user.getPhone());
    }


    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));
    }

    public String deleteUser(Long id) {
        userRepository.deleteById(id);
        return "Delete Success";
    }

    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email).orElseThrow(() -> new NotFoundException("Not found user !!"));
    }


    public LoginResponse refreshToken(String token) {
        String email = jwtService.extractUsername(token, REFRESH_SECRET_KEY);
        if (email.isEmpty()) {
            throw new TokenRefreshException(token, "Refresh token was expired. Please make a new sign in request");
        }
        if (Objects.requireNonNull(template.opsForValue().get(email)).isEmpty()) {
            throw new TokenRefreshException(token, "Jwt is not exist in redis");
        }
        long expiredAccess = 45 * 1000;
        long expiredRefresh = 365;
        String accessToken = jwtService.generateToken(email, expiredAccess, ACCESS_SECRET_KEY);
        String refreshToken = jwtService.generateRefreshToken(email, expiredRefresh, REFRESH_SECRET_KEY);
        return new LoginResponse(accessToken, refreshToken);
    }
}
