package com.code_room.auth_service.domain.usecases;

import com.code_room.auth_service.domain.ports.SendEmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class SendEmailServiceImpl implements SendEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

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




    private String generateRegistrationSuccessBody(String name, String code) {
        return "<!DOCTYPE html>" +
                "<html><head><style>" +
                "body { font-family: Arial, sans-serif; margin: 20px; color: #333; }" +
                ".container { background-color: #eef4ff; padding: 30px; border-radius: 10px; border: 1px solid #ccc; }" +
                "h2 { color: #1f3b75; }" +
                "p { font-size: 16px; line-height: 1.6; }" +
                ".code-box { margin-top: 20px; padding: 15px; background: #dceeff; border-radius: 8px; font-size: 20px; font-weight: bold; text-align: center; color: #0a3d62; }" +
                ".footer { margin-top: 30px; font-size: 12px; color: #888; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<h2>¡Hola, " + name + "! 👋</h2>" +
                "<p>¡Bienvenido a <strong>Code Room</strong>! 💻 Estamos muy emocionados de tenerte como parte de nuestra comunidad de desarrolladores. 🤍</p>" +
                "<p>Tu cuenta ha sido registrada correctamente. Para completar tu registro, por favor usa el siguiente código de verificación:</p>" +
                "<div class='code-box'>" + code + "</div>" +
                "<p>Si no fuiste tú quien realizó esta solicitud, puedes ignorar este mensaje.</p>" +
                "<p>¡Gracias por formar parte de <strong>Code Room</strong>! 🚀</p>" +
                "<p><strong>– El equipo de Code Room 🧠</strong></p>" +
                "<div class='footer'>Este es un mensaje automático. Por favor, no respondas a este correo.</div>" +
                "</div></body></html>";
    }

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
