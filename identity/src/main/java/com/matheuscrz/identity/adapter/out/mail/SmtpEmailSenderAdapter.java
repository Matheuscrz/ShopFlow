package com.matheuscrz.identity.adapter.out.mail;

import com.matheuscrz.identity.application.port.out.EmailSenderPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmtpEmailSenderAdapter implements EmailSenderPort {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.mail.logo-url}")
    private String logoUrl;

    @Override
    public void sendWelcomeEmail(String destinationEmail, String userName) {
        String subject = "Bem-vindo à nossa plataforma!";
        String htmlBody = """
                <div style="font-family: Arial, sans-serif; text-align: center; color: #333; padding: 30px; background-color: #f9f9f9;">
                    <div style="background-color: #ffffff; max-width: 600px; margin: 0 auto; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                        <img src="%s" alt="Shopflow Logo" style="max-width: 150px; margin-bottom: 20px;" />
                        <h2 style="color: #2c3e50;">Olá, %s!</h2>
                        <p style="font-size: 16px; line-height: 1.5;">Sua conta foi criada com sucesso. Estamos muito felizes em tê-lo conosco!</p>
                        <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;" />
                        <p style="font-size: 12px; color: #777;">Equipe de Suporte</p>
                    </div>
                </div>
                """
                .formatted(logoUrl, userName);

        sendHtmlEmail(destinationEmail, subject, htmlBody);
    }

    @Override
    public void sendPasswordChangedEmail(String destinationEmail, String userName) {
        String subject = "Aviso: Sua senha foi alterada";
        String htmlBody = """
                <div style="font-family: Arial, sans-serif; text-align: center; color: #333; padding: 30px; background-color: #f9f9f9;">
                    <div style="background-color: #ffffff; max-width: 600px; margin: 0 auto; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                        <img src="%s" alt="Shopflow Logo" style="max-width: 150px; margin-bottom: 20px;" />
                        <h2 style="color: #2c3e50;">Olá, %s!</h2>
                        <p style="font-size: 16px; line-height: 1.5;">A senha da sua conta foi alterada recentemente.</p>
                        <p style="font-size: 16px; line-height: 1.5; color: #e74c3c;">Se não foi você quem realizou esta ação, entre em contato imediatamente com a nossa equipe de segurança.</p>
                        <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;" />
                        <p style="font-size: 12px; color: #777;">Equipe de Segurança</p>
                    </div>
                </div>
                """
                .formatted(logoUrl, userName);

        sendHtmlEmail(destinationEmail, subject, htmlBody);
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);

            log.info("E-mail enviado com sucesso para {}", to);
        } catch (MessagingException | MailException e) {
            log.error("Falha ao enviar e-mail para {}", to, e);
            throw new IllegalStateException("Falha na entrega do e-mail", e);
        }
    }

    @Override
    public void sendPasswordResetEmail(String destinationEmail, String userName, String resetLink) {
        String subject = "Recuperação de Senha solicitada";
        String safeUserName = HtmlUtils.htmlEscape(userName);
        String safeResetLink = HtmlUtils.htmlEscape(resetLink);

        String htmlBody = """
                <div style="font-family: Arial, sans-serif; text-align: center; color: #333; padding: 30px; background-color: #f9f9f9;">
                    <div style="background-color: #ffffff; max-width: 600px; margin: 0 auto; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                        <img src="%s" alt="Shopflow Logo" style="max-width: 150px; margin-bottom: 20px;" />
                        <h2>Olá, %s!</h2>
                        <p style="font-size: 16px; line-height: 1.5;">Recebemos uma solicitação para redefinir a senha da sua conta.</p>
                        <p style="font-size: 16px; line-height: 1.5;">Clique no botão abaixo para escolher uma nova senha. O link é válido por 15 minutos.</p>
                        <a href="%s">Redefinir Senha</a>
                        <p style="font-size: 14px; color: #777;">Se você não solicitou a alteração, ignore este e-mail. Nenhuma mudança foi feita na sua conta.</p>
                        <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;" />
                        <p style="font-size: 12px; color: #777;">Equipe de Suporte</p>
                    </div>
                </div>
                """
                .formatted(logoUrl, safeUserName, safeResetLink);

        sendHtmlEmail(destinationEmail, subject, htmlBody);
    }
}