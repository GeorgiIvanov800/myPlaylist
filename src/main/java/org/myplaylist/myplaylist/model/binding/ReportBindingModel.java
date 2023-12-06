package org.myplaylist.myplaylist.model.binding;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ReportBindingModel {
    @NotEmpty(message = "Description cannot be empty.")
    @Size(min = 10, max = 50, message = "Please try to describe why do you report this comment with more than 10 characters")
    private String description;
    @NotEmpty(message = "Please give a reason for the report.Reports without good reason will be ignored")
    private String reason;
    private  Long commentId;

    public ReportBindingModel() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
}
