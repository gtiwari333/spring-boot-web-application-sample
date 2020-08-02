package gt.app.web.rest;

import gt.api.email.EmailClient;
import gt.api.email.EmailDto;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("tests")
@AllArgsConstructor
@Profile("dev")
public class TestResource {

    final EmailClient emailClient;

    @GetMapping("/email")
    void testEmail() {

        EmailDto email = new EmailDto();
        email.setTo(List.of("email@local.com"));
        email.setFrom("email@local.com");

        var filesBArray = new EmailDto.FileBArray[1];
        for (int i = 0; i < 1; i++) {
            filesBArray[i] = new EmailDto.FileBArray("uplFile.getBytes()".getBytes(), "testfile.txt");
        }
        email.setFiles(filesBArray);

        email.setSubject("hello");
        email.setContent("hello");
        emailClient.sendEmailWithAttachments(email);
    }
}
