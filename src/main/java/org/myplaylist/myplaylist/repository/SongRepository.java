package org.myplaylist.myplaylist.repository;

import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<SongEntity, Long> {


}
