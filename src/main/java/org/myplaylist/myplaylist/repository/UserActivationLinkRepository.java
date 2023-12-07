package org.myplaylist.myplaylist.repository;

import org.myplaylist.myplaylist.model.entity.UserActivationLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserActivationLinkRepository extends JpaRepository<UserActivationLinkEntity, Long> {
    UserActivationLinkEntity findByActivationLink(String token);
}
