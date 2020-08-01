package gt.mail.modules.email;

import gt.mail.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.UUID;

import static gt.mail.utils.EmailUtil.toInetArray;
import static gt.mail.utils.EmailUtil.toPlainStr;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final QueuedEmailRepository queuedEmailRepository;
    private final AppProperties appProperties;

    @Transactional
    public void queue(Collection<InternetAddress> to, Collection<InternetAddress> cc, Collection<InternetAddress> bcc, InternetAddress fromAddress,
                      String subject, String content, MultipartFile[] files, boolean isHtml) {

        try {
            QueuedEmail email = new QueuedEmail();

            email.setRecipient(new QueuedEmailRecipient(toPlainStr(to), toPlainStr(cc), toPlainStr(bcc)));
            email.setSubject(subject);
            email.setContent(content);
            email.setContentType(isHtml ? QueuedEmail.ContentType.HTML : QueuedEmail.ContentType.PLAIN);
            email.setFrom(EmailAddress.from(fromAddress.getAddress(), fromAddress.getPersonal()));


            for (MultipartFile file : files) {
                String newFileName = UUID.randomUUID() + "_" + file.getName();
                saveEmailAttachment(file, newFileName);

                var attachment = new QueuedEmailAttachment();
                attachment.setFileName(file.getName());
                attachment.setSavedFileName(newFileName);
                email.addAttachment(attachment);
            }

            queuedEmailRepository.save(email);

            log.debug("Queued e-mail, id : {}", email.getId());
        } catch (IOException e) {
            throw new RuntimeException("Failed to queue email", e);
        }
    }


    public void deQueue(QueuedEmail email) throws UnsupportedEncodingException, MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

        message.setTo(toInetArray(email.getRecipient().getTos()));
        message.setCc(toInetArray(email.getRecipient().getCcs()));
        message.setBcc(toInetArray(email.getRecipient().getBccs()));
        message.setFrom(email.getFrom().getEmail(), email.getFrom().getName());

        message.setSubject(email.getSubject());
        message.setText(email.getContent(), email.getContentType() == QueuedEmail.ContentType.HTML);

        for (var file : email.getAttachments()) {
            message.addAttachment(file.getFileName(), getEmailAttachment(file.getSavedFileName()));
        }

        javaMailSender.send(mimeMessage);

        log.debug("Sent queued e-mail id : {}", email.getId());

    }


    public File getEmailAttachment(String savedFileName) {
        return new File(appProperties.getFileStorage().getTempFolder() + File.separator + "emails", savedFileName);
    }

    public void saveEmailAttachment(MultipartFile file, String savedFileName) throws IOException {

        File copyTo = new File(appProperties.getFileStorage().getTempFolder() + File.separator + "emails", savedFileName);
        try {
            copyTo.getParentFile().mkdirs();
        } catch (SecurityException ex) {
            log.error("Could not create directory " + ex.getMessage(), ex);
            throw ex;
        }

        file.transferTo(copyTo);

    }

}
