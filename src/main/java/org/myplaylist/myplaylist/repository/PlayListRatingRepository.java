package org.myplaylist.myplaylist.repository;

import org.myplaylist.myplaylist.model.entity.PlaylistRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayListRatingRepository extends JpaRepository<PlaylistRatingEntity, Long> {

}
