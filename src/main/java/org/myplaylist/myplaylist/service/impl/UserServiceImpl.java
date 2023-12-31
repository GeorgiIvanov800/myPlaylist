package org.myplaylist.myplaylist.service.impl;

import org.myplaylist.myplaylist.config.mapper.UserMapper;
import org.myplaylist.myplaylist.exception.ObjectNotFoundException;
import org.myplaylist.myplaylist.model.binding.UserRegistrationBindingModel;
import org.myplaylist.myplaylist.model.entity.UserActivationLinkEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.entity.UserRoleEntity;
import org.myplaylist.myplaylist.model.enums.UserRoleEnum;
import org.myplaylist.myplaylist.model.enums.UserStatus;
import org.myplaylist.myplaylist.model.event.UserRegisterEvent;
import org.myplaylist.myplaylist.repository.UserActivationLinkRepository;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.myplaylist.myplaylist.repository.UserRoleRepository;
import org.myplaylist.myplaylist.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

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
    public UserStatus getUserStatus(String email) {
        UserEntity byEmail = userRepository.findByEmail(email)
                .orElse(null);

        if (byEmail == null) {
            return UserStatus.NOT_FOUND;
        } else if (!byEmail.isActive()) {
            return UserStatus.INACTIVE;
        } else {
            return UserStatus.ACTIVE;
        }
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
    public void addOrRemoveRole(Long userId, Long roleId, String action) {
        //find the User
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id:" + userId + " not found"));

        //Find the role to add or remove
        UserRoleEntity role = userRoleRepository.findById(roleId)
                .orElseThrow(() -> new ObjectNotFoundException("Role with id:" + roleId + " not found"));

        switch (action) {
            case "addRole":
                addRoleToUser(user, role);
                break;
            case "removeRole":
                removeRoleFromUser(user, role);
                break;
            default:
                LOGGER.error("Unknown action: " + action);
                break;
        }
    }

    private void addRoleToUser(UserEntity user, UserRoleEntity role) {
        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            userRepository.save(user);
            LOGGER.info("Role " + role.getRole() + " added to user " + user.getEmail());
        } else {
            LOGGER.error("User " + user.getEmail() + " already has the role: " + role.getRole());
        }
    }
    private void removeRoleFromUser(UserEntity user, UserRoleEntity role) {
        if (user.getRoles().size() <= 1) {
            LOGGER.error("Cannot remove the last role from user " + user.getEmail());
            return;
        }

        if (user.getRoles().remove(role)) {
            userRepository.save(user);
            LOGGER.info("Role " + role.getRole() + " removed from user " + user.getEmail());
        } else {
            LOGGER.error("User " + user.getEmail() + " does not have the role: " + role.getRole());
        }
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

    @Override
    public boolean isActive(String email) {
        Optional<UserEntity> byEmail = userRepository.findByEmail(email);
        return byEmail.get().isActive();
    }

    private boolean isAdmin(UserEntity userEntity) {

        return userEntity
                .getRoles()
                .stream()
                .map(UserRoleEntity::getRole)
                .anyMatch(r -> UserRoleEnum.ADMIN == r);

    }
}