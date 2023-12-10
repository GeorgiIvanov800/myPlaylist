package org.myplaylist.myplaylist.repository;

import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface PlaylistRepository extends JpaRepository<PlaylistEntity, Long> {

    Page<PlaylistEntity> findByUserId(Long user_id, Pageable pageable);

    @Query("SELECT COUNT(songs) FROM PlaylistEntity playlist JOIN playlist.songs songs WHERE playlist.user.id = :userId")
    Long countTotalSongsByUserId(Long userId);

    Optional<PlaylistEntity> findById(Long id);

    Page<PlaylistEntity> findByIsPrivateFalseAndCreatedOnAfter(LocalDateTime oneWeekAgo, Pageable pageable);



    @Query("SELECT p, " +
            "SUM(CASE WHEN r.ratingType = 'LIKE' THEN 1 ELSE 0 END) AS likes, " +
            "SUM(CASE WHEN r.ratingType = 'DISLIKE' THEN 1 ELSE 0 END) AS dislikes " +
            "FROM PlaylistEntity p " +
            "LEFT JOIN p.ratings r " +
            "GROUP BY p " +
            "ORDER BY SUM(CASE WHEN r.ratingType = 'LIKE' THEN 1 ELSE 0 END) - SUM(CASE WHEN r.ratingType = 'DISLIKE' THEN 1 ELSE 0 END) DESC")
    List<PlaylistEntity> findTopRatedPlaylists(Pageable pageable);

    Page<PlaylistEntity> findAll(Pageable pageable);
    @Query("SELECT p FROM PlaylistEntity p WHERE p.isPrivate = false AND (lower(p.name) LIKE lower(concat('%', :query, '%')) OR lower(p.genre) LIKE lower(concat('%', :query, '%')))")
    Page<PlaylistEntity> findBySearchQuery(@Param("query") String query, Pageable pageable);

}
