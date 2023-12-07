package org.myplaylist.myplaylist.service.impl;

import org.myplaylist.myplaylist.config.mapper.UserMapper;
import org.myplaylist.myplaylist.exception.ObjectNotFoundException;
import org.myplaylist.myplaylist.model.binding.UserRegistrationBindingModel;
import org.myplaylist.myplaylist.model.entity.UserActivationLinkEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.entity.UserRoleEntity;
import org.myplaylist.myplaylist.model.enums.UserRoleEnum;
import org.myplaylist.myplaylist.model.event.UserRegisterEvent;
import org.myplaylist.myplaylist.repository.UserActivationLinkRepository;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.myplaylist.myplaylist.repository.UserRoleRepository;
import org.myplaylist.myplaylist.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserActivationLinkRepository userActivationLinkRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher appEventPublisher;
    private final UserDetailsService userDetailsService;

    public UserServiceImpl(UserRepository userRepository,
                           UserActivationLinkRepository userActivationLinkRepository,
                           UserRoleRepository userRoleRepository,
                           PasswordEncoder passwordEncoder,
                           UserMapper userMapper,
                           ApplicationEventPublisher appEventPublisher,
                           UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.userActivationLinkRepository = userActivationLinkRepository;
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

    @Override
    public UserEntity findById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Unknown user with id:" + id));
    }

    @Override
    public String finByEmail(String email) {
        return userRepository.findByEmail(email)
                .stream()
                .findFirst()
                .map(UserEntity::getUsername)
                .orElse(null);

    }

    @Override
    public boolean activateUser(String token) {
        // Find the UserActivationLinkEntity by the provided token
        UserActivationLinkEntity userActivationLink = userActivationLinkRepository.findByActivationLink(token);

        if (userActivationLink != null) {

            UserEntity user = userActivationLink.getUser();

            if (!user.isActive()) {
                user.setActive(true);
                userRepository.save(user);
                userActivationLinkRepository.delete(userActivationLink);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUserActive(String email) {
        Optional<UserEntity> byEmail = userRepository.findByEmail(email);
        return byEmail.get().isActive();
    }

    @Override
    public Page<UserEntity> getAllUsers(Pageable pageable) {
        return userRepository.findAllBy(pageable);
    }

    @Override
    public List<UserRoleEntity> getAllRoles() {
        return userRoleRepository.findAll();
    }

    @Override
    public void addRole(Long userId, Long roleId) {
        //find the User
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id:" + userId + " not found"));
        //Find the role to add
        UserRoleEntity role = userRoleRepository.findById(roleId)
                .orElseThrow(() -> new ObjectNotFoundException("Role with id:" + roleId + " not found"));
        //add the role to the corresponding user
        user.getRoles()
                .add(role);

    }

    @Override
    public boolean isAdmin(String email) {
        return isAdmin(
                userRepository.findByEmail(email)
                        .orElseThrow(
                                () -> new ObjectNotFoundException("User with email: " + email + " not found")
                        )
        );
    }

    private boolean isAdmin(UserEntity userEntity) {

        return userEntity
                .getRoles()
                .stream()
                .map(UserRoleEntity::getRole)
                .anyMatch(r -> UserRoleEnum.ADMIN == r);

    }


}