package org.myplaylist.myplaylist.repository;

import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.service.impl.MyPlaylistUserDetailService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);
}
