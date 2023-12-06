package org.myplaylist.myplaylist.repository;

import org.myplaylist.myplaylist.model.entity.CommentEntity;
import org.myplaylist.myplaylist.model.view.CommentViewModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findAllByPlaylistId(Long id);
}
