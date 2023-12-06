package org.myplaylist.myplaylist.repository;

import org.myplaylist.myplaylist.model.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    boolean existsByCommentEntity_IdAndReportedBy_Email(Long commentId, String userEmail);

}
