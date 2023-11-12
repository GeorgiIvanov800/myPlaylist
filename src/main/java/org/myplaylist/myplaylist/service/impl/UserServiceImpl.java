package org.myplaylist.myplaylist.service.impl;

import org.myplaylist.myplaylist.config.UserMapper;
import org.myplaylist.myplaylist.exception.CustomValidationException;
import org.myplaylist.myplaylist.exception.LoginCredentialsException;
import org.myplaylist.myplaylist.model.binding.UserLoginBindingModel;
import org.myplaylist.myplaylist.model.binding.UserRegistrationBindingModel;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.myplaylist.myplaylist.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public void registerUser(UserRegistrationBindingModel userRegistrationBindingModel) {

        if (userRepository.findByEmail(userRegistrationBindingModel.getEmail()).isPresent()) {
            throw new CustomValidationException("Email already in use: " + userRegistrationBindingModel.getEmail(), "email");

        }

        if (userRepository.findByUsername(userRegistrationBindingModel.getUsername()).isPresent()) {
            throw new CustomValidationException("Username already in use: " + userRegistrationBindingModel.getUsername(), "username");

        }

        UserEntity user = userMapper.userDTOtoUserEntity(userRegistrationBindingModel);
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
    }


    @Override
    public void login(UserLoginBindingModel userLoginBindingModel) throws LoginCredentialsException {
        String email = userLoginBindingModel.getEmail();

        UserEntity user = userRepository
                .findByEmail(email)
                .orElseThrow( () -> new LoginCredentialsException("Email not found" + email) );

        boolean passwordMath = passwordEncoder
                .matches(userLoginBindingModel.getPassword(),
                        user.getPassword());

        if (!passwordMath) {
            throw new LoginCredentialsException("Incorrect Password");
        }


    }
    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    @Override
    public boolean emailExists(String email) {
        boolean isPresent = userRepository.findByEmail(email).isPresent();
        System.out.println();
        return isPresent;
    }

    @Override
    public boolean usernameExists(String username) {
        boolean isPresent = userRepository.findByUsername(username).isPresent();
        System.out.println();
        return isPresent;
    }

}
