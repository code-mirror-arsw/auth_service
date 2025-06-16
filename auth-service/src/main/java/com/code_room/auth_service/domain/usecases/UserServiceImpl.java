package com.code_room.auth_service.domain.usecases;

import com.code_room.auth_service.domain.Exception.LoginException;
import com.code_room.auth_service.domain.mapper.userMapper.UserMapper;
import com.code_room.auth_service.domain.model.User;
import com.code_room.auth_service.domain.ports.SendEmailService;
import com.code_room.auth_service.domain.ports.UserService;
import com.code_room.auth_service.infrastructure.controller.dto.LoginDto;
import com.code_room.auth_service.infrastructure.controller.dto.UserDto;
import com.code_room.auth_service.infrastructure.repository.UserRepository;
import com.code_room.auth_service.infrastructure.repository.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SendEmailService sendEmailService;

    @Override
    public User findByEmail(String email){
        UserEntity entity = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User no found by email " + email));
        return userMapper.toModelFromEntity(entity);
    }

    @Override
    public User checkPassword(LoginDto loginDto) {
        UserEntity user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!user.isEnabled()){
            throw new LoginException(LoginException.ErrorType.USER_NOT_VERIFIED);
        }

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return userMapper.toModelFromEntity(user);
    }

    @Override
    public void registerUser(UserDto userDto, String password) {
        Optional<UserEntity> existingUser = userRepository.findByEmail(userDto.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User already exists with this email.");
        }

        String encodedPassword = passwordEncoder.encode(password);
        String verificationCode = generateVerificationCode();
        UserEntity userEntity = userMapper.toEntityFromDto(userDto);
        userEntity.setPassword(encodedPassword);
        userEntity.setVerificationCode(verificationCode);
        userEntity.setEnabled(false);

        userRepository.save(userEntity);

        sendEmailService.sendRegistrationSuccessEmail(userDto.getEmail(), userDto.getName(), verificationCode);
    }

    private String generateVerificationCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    @Override
    public void verifyUser(String code) {
        UserEntity user = userRepository.findByVerificationCode(code)
                        .orElseThrow(() -> new RuntimeException("code no found"));
        user.setEnabled(true);
        user.setVerificationCode(null);
        userRepository.save(user);
        sendEmailService.sendAlreadyVerifiedEmail(user.getEmail(), user.getName());

    }
}
