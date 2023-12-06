package org.myplaylist.myplaylist.web;

import org.myplaylist.myplaylist.model.entity.ReportEntity;
import org.myplaylist.myplaylist.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/admin")
@Controller
public class AdminController {
    private final ReportService reportService;

    public AdminController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/panel")
    public String adminPanel(Model model) {
        List<ReportEntity> reports = reportService.allReports();
        model.addAttribute("reports", reports);

        return "admin-panel";
    }
}


//@Controller
//@RequestMapping("/admin/reports")
//public class AdminReportController {
//
//    // Inject necessary services
//
//    @GetMapping("/deleteComment/{commentId}")
//    public String deleteComment(@PathVariable Long commentId, RedirectAttributes redirectAttributes) {
//        // Logic to delete the comment
//        // Add a success or error message to redirectAttributes
//        return "redirect:/admin/reports";
//    }
//
//    @GetMapping("/clearReport/{reportId}")
//    public String clearReport(@PathVariable Long reportId, RedirectAttributes redirectAttributes) {
//        // Logic to clear the report
//        // Add a success or error message to redirectAttributes
//        return "redirect:/admin/reports";
//    }
//}