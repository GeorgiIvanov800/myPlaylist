package org.myplaylist.myplaylist.repository;

import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<SongEntity, Long> {

    List<SongEntity> findAll();

}
