package org.myplaylist.myplaylist.web;

import org.myplaylist.myplaylist.model.entity.ReportEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.service.CommentService;
import org.myplaylist.myplaylist.service.ReportService;
import org.myplaylist.myplaylist.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/admin")
@Controller
public class AdminController {
    private final ReportService reportService;
    private final CommentService commentService;

    private final UserService userService;


    public AdminController(ReportService reportService, CommentService commentService, UserService userService) {
        this.reportService = reportService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/panel")
    public String adminPanel(Model model,
                             @PageableDefault(
                                     size = 10
                             ) Pageable pageable) {

        Page<UserEntity> users = userService.getAllUsers(pageable);
        List<ReportEntity> reports = reportService.allReports();

        model.addAttribute("reports", reports);
        model.addAttribute("users", users);

        return "admin-panel";
    }

    @PreAuthorize("@commentServiceImpl.isAdmin(#principal.username)")
    @DeleteMapping("/reports/deleteComment/{commentId}")
    public String deleteComment(@PathVariable("commentId") Long commentId,
                                @AuthenticationPrincipal UserDetails principal) {

        commentService.deleteCommentAndReport(commentId);

        return "redirect:/admin/panel";
    }

    @PreAuthorize("@commentServiceImpl.isAdmin(#principal.username)")
    @DeleteMapping("/reports/clearReport/{reportId}")
    public String clearReport(@PathVariable("reportId") Long reportId,
                              @AuthenticationPrincipal UserDetails principal) {
        reportService.deleteReport(reportId);
        return "redirect:/admin/panel";
    }
}