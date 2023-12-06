package org.myplaylist.myplaylist.service.impl;

import jakarta.validation.Valid;
import org.myplaylist.myplaylist.exception.ObjectNotFoundException;
import org.myplaylist.myplaylist.model.binding.ReportBindingModel;
import org.myplaylist.myplaylist.model.entity.CommentEntity;
import org.myplaylist.myplaylist.model.entity.ReportEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.entity.UserRoleEntity;
import org.myplaylist.myplaylist.model.enums.UserRoleEnum;
import org.myplaylist.myplaylist.repository.CommentRepository;
import org.myplaylist.myplaylist.repository.ReportRepository;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.myplaylist.myplaylist.service.ReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public ReportServiceImpl(ReportRepository reportRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }


    @Override
    public void createReport(ReportBindingModel reportBindingModel, String email) {

        CommentEntity comment = commentRepository.findById(reportBindingModel.getCommentId())
                .orElseThrow( () -> new ObjectNotFoundException("Comment with not found"));
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("User with not found"));


        ReportEntity report = new ReportEntity();
        report.setDescription(reportBindingModel.getDescription());
        report.setReportedOn(LocalDateTime.now());
        report.setReason(reportBindingModel.getReason());
        report.setCommentEntity(comment);
        report.setReportedBy(user);


        reportRepository.save(report);
    }

    @Override
    public boolean hasUserAlreadyReportedComment(Long commentId, String userEmail) {
        return reportRepository.existsByCommentEntity_IdAndReportedBy_Email(commentId, userEmail);
    }

    @Override
    public List<ReportEntity> allReports() {
        return reportRepository.findAll();
    }

    @Override
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

    @Override
    public boolean isAdmin(String email) {
        return isAdmin(
                userRepository.findByEmail(email)
                        .orElseThrow(
                                () -> new ObjectNotFoundException("User with email: " + email + " cannot be found"))
        );
    }

    private boolean isAdmin(UserEntity user) {
        return user
                .getRoles()
                .stream()
                .map(UserRoleEntity::getRole)
                .anyMatch(r -> UserRoleEnum.ADMIN == r);
    }
}
