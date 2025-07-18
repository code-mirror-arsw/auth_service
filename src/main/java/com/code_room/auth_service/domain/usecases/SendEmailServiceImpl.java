package com.code_room.auth_service.domain.usecases;

import com.code_room.auth_service.domain.ports.SendEmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Service implementation responsible for sending email notifications related to user authentication.
 * Implements the {@link SendEmailService} interface.
 */
@Service
public class SendEmailServiceImpl implements SendEmailService {

    /**
     * JavaMailSender used to create and send MIME emails.
     */
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Email address used as the sender, configured via application properties.
     */
    @Value("${spring.mail.username}")
    private String from;

    /**
     * Sends a registration success email containing a verification code to the specified recipient.
     *
     * @param to               the recipient's email address
     * @param name             the recipient's name for personalization in the email
     * @param verificationCode the verification code required to complete registration
     * @throws RuntimeException if an error occurs while sending the email
     */
    @Override
    public void sendRegistrationSuccessEmail(String to, String name, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("🎉 ¡Bienvenido a Code Room!");
            helper.setFrom(from);

            String htmlBody = generateRegistrationSuccessBody(name, verificationCode);
            helper.setText(htmlBody, true);

            mailSender.send(message);

            System.out.println("Registration email sent to " + to);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error sending registration email", e);
        }
    }

    /**
     * Sends an email notifying the recipient that their account has already been verified.
     *
     * @param to   the recipient's email address
     * @param name the recipient's name for personalization in the email
     * @throws RuntimeException if an error occurs while sending the email
     */
    @Override
    public void sendAlreadyVerifiedEmail(String to, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("✅ Your account is already verified – Code Room");
            helper.setFrom(from);

            String htmlBody = generateAlreadyVerifiedBody(name);
            helper.setText(htmlBody, true);

            mailSender.send(message);

            System.out.println("Already verified email sent to " + to);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error sending already verified email", e);
        }
    }

    /**
     * Generates the HTML content for the registration success email.
     *
     * @param name the recipient's name to personalize the email
     * @param code the verification code to be included in the email
     * @return a String containing the full HTML email body
     */
    private String generateRegistrationSuccessBody(String name, String code) {
        return "<!DOCTYPE html>" +
                "<html><head><style>" +
                "body { font-family: Arial, sans-serif; margin: 20px; color: #333; }" +
                ".container { background-color: #eef4ff; padding: 30px; border-radius: 10px; border: 1px solid #ccc; }" +
                "h2 { color: #1f3b75; }" +
                "p { font-size: 16px; line-height: 1.6; }" +
                ".code-box { margin-top: 20px; padding: 15px; background: #dceeff; border-radius: 8px; font-size: 20px; font-weight: bold; text-align: center; color: #0a3d62; }" +
                ".btn { display:inline-block; margin-top:25px; padding:12px 20px; background:#1f3b75; color:#fff; text-decoration:none; border-radius:6px; font-weight:600; }" +
                ".footer { margin-top: 30px; font-size: 12px; color: #888; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<h2>¡Hola, " + name + "! 👋</h2>" +
                "<p>¡Bienvenido a <strong>Code Room</strong>! 💻 Estamos muy emocionados de tenerte como parte de nuestra comunidad de desarrolladores. 🤍</p>" +
                "<p>Tu cuenta ha sido registrada correctamente. Para completar tu registro, por favor usa el siguiente código de verificación</p>" +
                "<div class='code-box'>" + code + "</div>" +
                "<p>En el siguiente link</p> " +
                "<a href='http://localhost:5173/verificacion' class='btn'>http://localhost:5173/verificacion</a>" +
                "<p style='margin-top:25px;'>Si no fuiste tú quien realizó esta solicitud, puedes ignorar este mensaje.</p>" +
                "<p>¡Gracias por formar parte de <strong>Code Room</strong>! 🚀</p>" +
                "<p><strong>– El equipo de Code Room 🧠</strong></p>" +
                "<div class='footer'>Este es un mensaje automático. Por favor, no respondas a este correo.</div>" +
                "</div></body></html>";
    }

    /**
     * Generates the HTML content for the email informing the user their account is already verified.
     *
     * @param name the recipient's name to personalize the email
     * @return a String containing the full HTML email body
     */
    private String generateAlreadyVerifiedBody(String name) {
        return "<!DOCTYPE html>" +
                "<html><head><style>" +
                "body { font-family: Arial, sans-serif; margin: 20px; color: #333; }" +
                ".container { background-color: #eafaf1; padding: 30px; border-radius: 10px; border: 1px solid #ccc; }" +
                "h2 { color: #1a7f5f; }" +
                "p { font-size: 16px; line-height: 1.6; }" +
                ".footer { margin-top: 30px; font-size: 12px; color: #888; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<h2>Hola, " + name + " 👋</h2>" +
                "<p>Hemos detectado que tu cuenta ya fue verificada anteriormente. ✅</p>" +
                "<p>Ya puedes iniciar sesión y disfrutar de todas las funcionalidades de <strong>Code Room</strong>. 💻🚀</p>" +
                "<p>Si no fuiste tú quien solicitó esto, por favor contáctanos inmediatamente.</p>" +
                "<p><strong>– El equipo de Code Room 🧠</strong></p>" +
                "<div class='footer'>Este es un mensaje automático. Por favor, no respondas a este correo.</div>" +
                "</div></body></html>";
    }

}
