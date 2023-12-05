package org.myplaylist.myplaylist.service.impl;

import org.myplaylist.myplaylist.config.mapper.UserMapper;
import org.myplaylist.myplaylist.model.binding.UserRegistrationBindingModel;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.entity.UserRoleEntity;
import org.myplaylist.myplaylist.model.enums.UserRoleEnum;
import org.myplaylist.myplaylist.model.event.UserRegisterEvent;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.myplaylist.myplaylist.repository.UserRoleRepository;
import org.myplaylist.myplaylist.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher appEventPublisher;
    private final UserDetailsService userDetailsService;

    public UserServiceImpl(UserRepository userRepository,
                           UserRoleRepository userRoleRepository,
                           PasswordEncoder passwordEncoder,
                           UserMapper userMapper,
                           ApplicationEventPublisher appEventPublisher,
                           UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.appEventPublisher = appEventPublisher;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void registerUser(UserRegistrationBindingModel userRegistrationBindingModel) {

        //Map dto to entity
        UserEntity user = userMapper.userDTOtoUserEntity(userRegistrationBindingModel);

        //Create a role entity every new user should have the role of USER
        UserRoleEntity userRole = userRoleRepository.findByRole(UserRoleEnum.USER);

        user.setActive(false); // false by default, until the user activates the account
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRegisterDate(LocalDateTime.now());
        user.getRoles().add(userRole);

        //Save the user in the DB
        userRepository.save(user);

        appEventPublisher.publishEvent(new UserRegisterEvent(
                "UserService",
                userRegistrationBindingModel.getEmail(),
                userRegistrationBindingModel.fullName()
        ));
    }

    @Override
    public Authentication login(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        return auth;
    }

}