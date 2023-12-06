package org.myplaylist.myplaylist.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class ReportEntity extends BaseEntity {
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String reason;
    private LocalDateTime reportedOn;
    @ManyToOne
    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    private CommentEntity commentEntity;
    @ManyToOne
    @JoinColumn(name = "reported_by_user_id", referencedColumnName = "id")
    private UserEntity reportedBy;

    public String getDescription() {
        return description;
    }

    public void setDescription(String content) {
        this.description = content;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getReportedOn() {
        return reportedOn;
    }

    public void setReportedOn(LocalDateTime reportedOn) {
        this.reportedOn = reportedOn;
    }

    public CommentEntity getCommentEntity() {
        return commentEntity;
    }

    public void setCommentEntity(CommentEntity commentEntity) {
        this.commentEntity = commentEntity;
    }

    public UserEntity getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(UserEntity reportedBy) {
        this.reportedBy = reportedBy;
    }
}
