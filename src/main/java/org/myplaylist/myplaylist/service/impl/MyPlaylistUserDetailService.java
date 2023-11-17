package org.myplaylist.myplaylist.service.impl;

import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.entity.UserRoleEntity;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

public class MyPlaylistUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public MyPlaylistUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //TODO:
        System.out.println();
        return userRepository.findByEmail(email)
                .map(this::map)
                .orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found"));
    }

    private UserDetails map(UserEntity userEntity) {

        List<GrantedAuthority> authorities = userEntity.getRoles()
                .stream()
                .map(MyPlaylistUserDetailService::map)
                .collect(Collectors.toList());

        return new CustomUserDetails(
                userEntity.getEmail(), // Login via email
                userEntity.getPassword(),
                authorities,
                userEntity.getUsername(),
                userEntity.getId()
        );

    }
    private static GrantedAuthority map(UserRoleEntity userRoleEntity) {
        return new SimpleGrantedAuthority(
                "ROLE_" + userRoleEntity.getRole().name()
        );
    }
}
