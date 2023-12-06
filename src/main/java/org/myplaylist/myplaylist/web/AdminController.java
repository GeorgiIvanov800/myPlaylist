package org.myplaylist.myplaylist.web;

import org.myplaylist.myplaylist.model.entity.ReportEntity;
import org.myplaylist.myplaylist.service.CommentService;
import org.myplaylist.myplaylist.service.ReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequestMapping("/admin")
@Controller
public class AdminController {
    private final ReportService reportService;

    private final CommentService commentService;


    public AdminController(ReportService reportService, CommentService commentService) {
        this.reportService = reportService;

        this.commentService = commentService;
    }

    @GetMapping("/panel")
    public String adminPanel(Model model) {
        List<ReportEntity> reports = reportService.allReports();
        model.addAttribute("reports", reports);

        return "admin-panel";
    }

    @PreAuthorize("@commentServiceImpl.isAdmin(#principal.username)")
    @DeleteMapping("/reports/deleteComment/{commentId}")
    public String deleteComment(@PathVariable ("commentId") Long commentId,
                                @AuthenticationPrincipal UserDetails principal) {
        System.out.println();
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