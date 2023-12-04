package org.myplaylist.myplaylist.repository;

import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;


public interface PlaylistRepository extends JpaRepository<PlaylistEntity, Long> {

    Page<PlaylistEntity> findByUserId(Long userId, Pageable pageable);
    @Query("SELECT COUNT(songs) FROM PlaylistEntity playlist JOIN playlist.songs songs WHERE playlist.user.id = :userId")
    Long countTotalSongsByUserId(Long userId);

    Optional<PlaylistEntity> findById(Long id);


}
