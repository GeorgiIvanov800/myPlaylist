package org.myplaylist.myplaylist.web;

import org.myplaylist.myplaylist.model.entity.ReportEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.entity.UserRoleEntity;
import org.myplaylist.myplaylist.service.CommentService;
import org.myplaylist.myplaylist.service.ReportService;
import org.myplaylist.myplaylist.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
        List<UserRoleEntity> roles = userService.getAllRoles();

        model.addAttribute("reports", reports);
        model.addAttribute("users", users);
        model.addAttribute("roles", roles);

        return "admin-panel";
    }

    @PreAuthorize("@commentServiceImpl.isAdmin(#principal.username)")
    @DeleteMapping("/reports/deleteComment/{commentId}") //TODO: make it with request param
    public String deleteComment(@PathVariable("commentId") Long commentId,
                                @AuthenticationPrincipal UserDetails principal) {

        commentService.deleteCommentAndReport(commentId);

        return "redirect:/admin/panel";
    }

//    @PreAuthorize("@commentServiceImpl.isAdmin(#principal.username)") TODO: Remove after the unit tests are successful
    @PreAuthorize("@userRoleChecker.isAdmin(#principal.name)")
    @DeleteMapping("/reports/clearReport/{reportId}")  //TODO: Try make it with Request Param
    public String clearReport(@PathVariable("reportId") Long reportId,
                              @AuthenticationPrincipal UserDetails principal) {
        reportService.deleteReport(reportId);
        return "redirect:/admin/panel";
    }
//    @PreAuthorize("@userServiceImpl.isAdmin(#principal.name)")
    @PreAuthorize("@userRoleChecker.isAdmin(#principal.name)")
    @PostMapping("/action")
    public String handleUserAction(@RequestParam Long userId,
                                   @RequestParam String action,
                                   @RequestParam(required = false) Long roleId,
                                   Principal principal) {

      userService.addOrRemoveRole(userId, roleId, action);

      return "redirect:/admin/panel";
    }
}