package com.example.campconnect_backend.service;

import com.example.campconnect_backend.model.Reservation;
import com.example.campconnect_backend.model.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.admin.email}")
    private String adminEmail;

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("MMM dd, yyyy");

    @Async
    public void sendReservationConfirmation(Reservation reservation) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String userEmail = reservation.getUser().getEmail();
            String userName  = reservation.getUser().getFirstName();
            String siteName  = reservation.getCampingSite().getName();
            String location  = reservation.getCampingSite().getLocation();
            String checkIn   = DATE_FMT.format(reservation.getStartDate());
            String checkOut  = DATE_FMT.format(reservation.getEndDate());

            helper.setTo(userEmail);
            helper.setSubject("Your CampConnect Reservation is Confirmed!");
            helper.setText(buildHtml(userName, siteName, location, checkIn, checkOut, reservation.getId()), true);

            mailSender.send(message);
        } catch (Exception e) {
            // Log but don't fail the reservation if mail fails
            System.err.println("Failed to send confirmation email: " + e.getMessage());
        }
    }

    private String buildHtml(String name, String site, String location,
                              String checkIn, String checkOut, Long reservationId) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="margin:0;padding:0;background:#f5f5f0;font-family:'Segoe UI',sans-serif;">
              <div style="max-width:560px;margin:40px auto;background:#ffffff;border-radius:24px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);">
                <!-- Header -->
                <div style="background:linear-gradient(135deg,#2d5a27,#4a7c59);padding:40px 40px 32px;text-align:center;">
                  <h1 style="color:#ffffff;margin:0;font-size:28px;font-weight:800;letter-spacing:-0.5px;">CampConnect</h1>
                  <p style="color:rgba(255,255,255,0.8);margin:8px 0 0;font-size:14px;">Your wilderness awaits</p>
                </div>
                <!-- Body -->
                <div style="padding:40px;">
                  <h2 style="color:#2d5a27;font-size:22px;margin:0 0 8px;">You're all set, %s!</h2>
                  <p style="color:#666;font-size:15px;margin:0 0 28px;">Your expedition has been confirmed. Here are your booking details:</p>
 
                  <div style="background:#f8f9f4;border-radius:16px;padding:24px;margin-bottom:28px;">
                    <p style="margin:0 0 4px;font-size:18px;font-weight:700;color:#1a1a1a;">%s</p>
                    <p style="margin:0 0 20px;color:#888;font-size:14px;">📍 %s</p>
                    <div style="display:flex;gap:16px;">
                      <div style="flex:1;background:#fff;border-radius:12px;padding:16px;">
                        <p style="margin:0 0 4px;font-size:10px;font-weight:700;text-transform:uppercase;letter-spacing:1px;color:#999;">Check-In</p>
                        <p style="margin:0;font-weight:700;color:#2d5a27;">%s</p>
                      </div>
                      <div style="flex:1;background:#fff;border-radius:12px;padding:16px;">
                        <p style="margin:0 0 4px;font-size:10px;font-weight:700;text-transform:uppercase;letter-spacing:1px;color:#999;">Check-Out</p>
                        <p style="margin:0;font-weight:700;color:#2d5a27;">%s</p>
                      </div>
                    </div>
                  </div>
 
                  <p style="color:#888;font-size:13px;text-align:center;margin:0;">Reservation ID: <strong>#%d</strong></p>
                </div>
                <!-- Footer -->
                <div style="background:#f8f9f4;padding:24px 40px;text-align:center;border-top:1px solid #eee;">
                  <p style="color:#aaa;font-size:12px;margin:0;">© 2026 CampConnect · Happy trails!</p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(name, site, location, checkIn, checkOut, reservationId);
    }

    @Async
    public void sendPaymentReminder(com.example.campconnect_backend.model.Reservation reservation) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            String userEmail = reservation.getUser().getEmail();
            String userName = reservation.getUser().getFirstName();
            String siteName = reservation.getCampingSite().getName();
            java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm");
            String deadline = fmt.format(reservation.getPaymentDeadline());

            helper.setTo(userEmail);
            helper.setSubject("⏳ Complete your payment for " + siteName);
            helper.setText("""
                <!DOCTYPE html><html><body style="font-family:'Segoe UI',sans-serif;background:#f5f5f0;margin:0;padding:0;">
                <div style="max-width:560px;margin:40px auto;background:#fff;border-radius:24px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);">
                  <div style="background:linear-gradient(135deg,#805533,#fdc39a);padding:32px 40px;text-align:center;">
                    <h1 style="color:#fff;margin:0;font-size:22px;font-weight:800;">Payment Required</h1>
                  </div>
                  <div style="padding:40px;">
                    <h2 style="color:#163526;font-size:20px;margin:0 0 8px;">Hi %s!</h2>
                    <p style="color:#666;font-size:15px;">Your reservation at <strong>%s</strong> is pending payment.</p>
                    <div style="background:#fef3c7;border-left:4px solid #f59e0b;padding:16px;border-radius:0 12px 12px 0;margin:20px 0;">
                      <p style="margin:0;font-weight:700;color:#92400e;">⏰ Pay before: %s</p>
                      <p style="margin:4px 0 0;color:#92400e;font-size:13px;">Your reservation will be automatically cancelled if payment is not received.</p>
                    </div>
                    <p style="color:#666;font-size:14px;">Total amount: <strong>$%.2f</strong></p>
                    <p style="color:#888;font-size:13px;text-align:center;margin-top:24px;">Go to My Trips to complete your payment.</p>
                  </div>
                </div></body></html>
                """.formatted(userName, siteName, deadline, reservation.getTotalPrice()), true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send payment reminder: " + e.getMessage());
        }
    }

    @Async
    public void sendPaymentExpiredNotification(com.example.campconnect_backend.model.Reservation reservation) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(reservation.getUser().getEmail());
            helper.setSubject("❌ Reservation cancelled — payment deadline passed");
            helper.setText("""
                <!DOCTYPE html><html><body style="font-family:'Segoe UI',sans-serif;background:#f5f5f0;margin:0;padding:0;">
                <div style="max-width:560px;margin:40px auto;background:#fff;border-radius:24px;overflow:hidden;">
                  <div style="background:#dc2626;padding:32px 40px;text-align:center;">
                    <h1 style="color:#fff;margin:0;font-size:22px;font-weight:800;">Reservation Cancelled</h1>
                  </div>
                  <div style="padding:40px;">
                    <p style="color:#666;font-size:15px;">Hi <strong>%s</strong>, your reservation at <strong>%s</strong> (Reservation #%d) has been automatically cancelled because the 24-hour payment window expired.</p>
                    <p style="color:#666;font-size:14px;">You can make a new reservation anytime on CampConnect.</p>
                  </div>
                </div></body></html>
                """.formatted(
                    reservation.getUser().getFirstName(),
                    reservation.getCampingSite().getName(),
                    reservation.getId()), true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send expiry notification: " + e.getMessage());
        }
    }

    @Async
    public void sendSafetyAlert(Review review) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String siteName = review.getCampingSite() != null && review.getCampingSite().getName() != null
                              ? review.getCampingSite().getName() : "Unknown Site";

            helper.setTo(adminEmail);
            helper.setSubject("🚨 Urgent Safety Alert — " + siteName);

            String html = """
                <!DOCTYPE html>
                <html>
                <body style="margin:0;padding:0;background:#fff5f5;font-family:'Segoe UI',sans-serif;">
                  <div style="max-width:560px;margin:40px auto;background:#ffffff;border-radius:24px;border:1px solid #fee2e2;overflow:hidden;box-shadow:0 4px 24px rgba(220,38,38,0.15);">
                    <div style="background:linear-gradient(135deg,#dc2626,#b91c1c);padding:32px 40px;text-align:center;">
                      <p style="color:rgba(255,255,255,0.8);margin:0 0 6px;font-size:12px;text-transform:uppercase;letter-spacing:2px;">CampConnect Admin</p>
                      <h1 style="color:#ffffff;margin:0;font-size:24px;font-weight:800;">⚠️ Safety Alert Detected</h1>
                    </div>
                    <div style="padding:40px;">
                      <p style="color:#1a1a1a;font-size:16px;font-weight:600;margin:0 0 20px;">
                        A review at <strong>%s</strong> has been automatically flagged by the safety system.
                      </p>
                      <div style="background:#fef2f2;border-left:4px solid #dc2626;padding:20px;margin-bottom:24px;border-radius:0 12px 12px 0;">
                        <p style="margin:0 0 8px;font-weight:700;color:#991b1b;font-size:11px;text-transform:uppercase;letter-spacing:1px;">🚩 Flag Reason</p>
                        <p style="margin:0 0 12px;color:#b91c1c;font-weight:600;">%s</p>
                        <p style="margin:0;color:#7f1d1d;font-style:italic;font-size:15px;">"%s"</p>
                      </div>
                      <p style="color:#666;font-size:14px;margin:0 0 8px;">Submitted by: <strong>%s</strong></p>
                      <p style="color:#666;font-size:14px;margin:0;">Rating given: <strong>%d / 5</strong></p>
                      <div style="margin-top:28px;padding-top:20px;border-top:1px solid #fee2e2;">
                        <p style="color:#999;font-size:12px;margin:0;">Please review this in the admin dashboard and take appropriate action.</p>
                      </div>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(
                    siteName,
                    review.getFlagReason(),
                    review.getComment(),
                    review.getUser() != null ? review.getUser().getFirstName() + " " + review.getUser().getLastName() : "Unknown",
                    review.getRating()
                );

            helper.setText(html, true);
            mailSender.send(message);
            System.out.println("Safety alert email sent to " + adminEmail);
        } catch (Exception e) {
            System.err.println("Failed to send safety alert email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
