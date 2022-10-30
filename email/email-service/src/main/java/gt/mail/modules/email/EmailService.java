package gt.mail.modules.email;

import gt.api.email.EmailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static gt.mail.utils.EmailUtil.toInetArray;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendEmail(EmailDto email) {

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            var message = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            message.setTo(toInetArray(email.getTo()));
            message.setCc(toInetArray(email.getCc()));
            message.setBcc(toInetArray(email.getBcc()));
            message.setFrom(email.getFromEmail(), email.getFromName());

            message.setSubject(email.getSubject());
            message.setText(email.getContent(), email.isHtml());

            for (var file : email.getFiles()) {
                message.addAttachment(file.getFilename(), new ByteArrayResource(file.getData()));
            }

            javaMailSender.send(mimeMessage);

            log.debug("Email Sent subject: {}", email.getSubject());
        } catch (IOException | MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

}
