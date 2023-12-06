package org.myplaylist.myplaylist.web;

import jakarta.validation.Valid;
import org.myplaylist.myplaylist.model.binding.ReportBindingModel;
import org.myplaylist.myplaylist.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @ModelAttribute
    ReportBindingModel reportBindingModel() {
        return new ReportBindingModel();
    }

    @GetMapping("/create/{commentId}")
    public String report(@PathVariable(name = "commentId")
                         Long commentId,
                         Principal principal,
                         Model model) {

        ReportBindingModel reportBindingModel = new ReportBindingModel();
        reportBindingModel.setCommentId(commentId);
        model.addAttribute("reportBindingModel", reportBindingModel);

        return "report-comment";
    }

    @PostMapping("/create/")
    public String reportPost(@Valid ReportBindingModel reportBindingModel,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model,
                             Principal principal) {
        String userEmail = principal.getName();

        if (reportService.hasUserAlreadyReportedComment(reportBindingModel.getCommentId(), userEmail)) {
            model.addAttribute("errorMessage", "You have already reported this comment");
            return "report-comment";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("reportBindingModel", reportBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.reportBindingModel", bindingResult);
            return "report-comment";
        }

        reportService.createReport(reportBindingModel, userEmail);
        redirectAttributes.addFlashAttribute("successMessage", "Your report has been successfully submitted.");

        return "redirect:/users/dashboard";
    }
}
