package org.myplaylist.myplaylist.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.myplaylist.myplaylist.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServiceImpl implements EmailService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    private final String myPlaylistEmail;

    public EmailServiceImpl(TemplateEngine templateEngine,
                            JavaMailSender javaMailSender,
                            @Value("${mail.myPlaylist}") String myPlaylistEmail) {
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
        this.myPlaylistEmail = myPlaylistEmail;
    }

    @Override
    public void sendRegistrationEmail(String userEmail, String userName, String activationLink)  {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        try {
            mimeMessageHelper.setTo(userEmail);
            mimeMessageHelper.setFrom(myPlaylistEmail);
            mimeMessageHelper.setReplyTo(myPlaylistEmail);
            mimeMessageHelper.setSubject("Welcome to MyPlaylist");
            mimeMessageHelper.setText(generateRegistrationEmailBody(userName, activationLink), true);

            javaMailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateRegistrationEmailBody(String username, String activationLink) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("activation_link", activationLink);
        return templateEngine.process("email/registration-email", context);
    }
}
