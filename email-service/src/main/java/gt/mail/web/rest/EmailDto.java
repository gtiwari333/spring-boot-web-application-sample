package gt.mail.web.rest;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

@Data
class EmailDto {

    String from;
    Collection<String> to;
    Collection<String> cc;
    Collection<String> bcc;
    String subject;
    String content;
    boolean isHtml;
    MultipartFile[] files;

}
