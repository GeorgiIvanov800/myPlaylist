package org.myplaylist.myplaylist.service;

import jakarta.validation.Valid;
import org.myplaylist.myplaylist.model.binding.ReportBindingModel;
import org.myplaylist.myplaylist.model.entity.ReportEntity;

import java.util.List;

public interface ReportService {

    void createReport(ReportBindingModel reportBindingModel,String user);
    boolean  hasUserAlreadyReportedComment(Long commentId, String userEmail);
    List<ReportEntity> allReports();
    void deleteReport(Long id);
    boolean isAdmin(String email);
}
