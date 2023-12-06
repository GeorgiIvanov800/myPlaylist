package org.myplaylist.myplaylist.service;

import jakarta.validation.Valid;
import org.myplaylist.myplaylist.model.binding.ReportBindingModel;

public interface ReportService {

    void createReport(ReportBindingModel reportBindingModel,String user);

    boolean  hasUserAlreadyReportedComment(Long commentId, String userEmail);
}
