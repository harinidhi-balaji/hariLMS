package com.hari.lms.controller;

import com.hari.lms.entity.Payment;
import com.hari.lms.entity.User;
import com.hari.lms.service.PaymentService;
import com.hari.lms.service.CourseService;
import com.hari.lms.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for payment-related operations.
 * 
 * @author Hari Parthu
 */
@Controller
@RequestMapping("/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private AuthService authService;

    /**
     * Show payment form for course enrollment.
     */
    @GetMapping("/course/{courseId}")
    public String showPaymentForm(@PathVariable Long courseId, Model model) {
        logger.info("Showing payment form for course {}", courseId);

        try {
            // Get course details
            var course = courseService.getCourse(courseId);
            model.addAttribute("course", course);

            return "payment/form";
        } catch (Exception e) {
            logger.error("Error showing payment form: {}", e.getMessage());
            model.addAttribute("errorMessage", "Course not found");
            return "redirect:/courses";
        }
    }

    /**
     * Process payment for course enrollment.
     */
    @PostMapping("/process/{courseId}")
    public String processPayment(@PathVariable Long courseId,
            @RequestParam String studentName,
            @RequestParam String studentEmail,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String agreeTerms,
            RedirectAttributes redirectAttributes) {
        logger.info("Processing enrollment for course {} - Student: {} ({})", courseId, studentName, studentEmail);

        try {
            // Get current user
            User currentUser = authService.getCurrentUserEntity();

            // Create payment with dummy processing (always successful)
            Payment payment = paymentService.createPayment(currentUser.getId(), courseId);
            payment.setPaymentMethod(paymentMethod);

            // Simulate instant successful payment
            Payment processedPayment = paymentService.processPayment(payment.getId());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Enrollment successful! Welcome to the course, " + studentName + "!");
            redirectAttributes.addFlashAttribute("studentName", studentName);
            redirectAttributes.addFlashAttribute("studentEmail", studentEmail);

            return "redirect:/payment/success/" + processedPayment.getId();

        } catch (Exception e) {
            logger.error("Error processing enrollment: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Enrollment failed: " + e.getMessage() + ". Please try again.");
            return "redirect:/payment/course/" + courseId;
        }
    }

    /**
     * Show payment success page.
     */
    @GetMapping("/success/{paymentId}")
    public String showPaymentSuccess(@PathVariable Long paymentId, Model model) {
        logger.info("Showing payment success page for payment {}", paymentId);

        try {
            Payment payment = paymentService.getPaymentById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            model.addAttribute("payment", payment);
            model.addAttribute("course", payment.getCourse());

            return "payment/success";
        } catch (Exception e) {
            logger.error("Error showing payment success: {}", e.getMessage());
            model.addAttribute("errorMessage", "Payment details not found");
            return "redirect:/courses";
        }
    }

    /**
     * Show payment failure page.
     */
    @GetMapping("/failure/{paymentId}")
    public String showPaymentFailure(@PathVariable Long paymentId, Model model) {
        logger.info("Showing payment failure page for payment {}", paymentId);

        try {
            Payment payment = paymentService.getPaymentById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            model.addAttribute("payment", payment);
            model.addAttribute("course", payment.getCourse());

            return "payment/failure";
        } catch (Exception e) {
            logger.error("Error showing payment failure: {}", e.getMessage());
            model.addAttribute("errorMessage", "Payment details not found");
            return "redirect:/courses";
        }
    }
}