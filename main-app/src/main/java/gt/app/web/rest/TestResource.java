package gt.app.web.rest;

import gt.api.email.EmailDto;
import gt.app.api.EmailClient;
import gt.app.config.security.CurrentUser;
import gt.app.config.security.CurrentUserToken;
import gt.app.modules.article.ArticleService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record7;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("tests")
@AllArgsConstructor
@Profile("dev")
@Slf4j
public class TestResource {

    final EmailClient emailClient;
    final ArticleService articleService;

    @GetMapping("/email")
    void testEmail() {

        log.debug("Sending email ...");

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

    @GetMapping("/jooq")
    ResponseEntity<List<Dto>> jooq(@CurrentUser CurrentUserToken u, Pageable pageable) {

        Page<Record7<Long, String, String, String, String, String, LocalDateTime>> page = articleService.testJooqPaginationQuery(pageable, u.getUserId());

        List<Dto> dtos = page.getContent().stream()
            .map(r -> new Dto(r.getValue(r.field1()), r.getValue(r.field2()), r.getValue(r.field3()), r.getValue(r.field4()), r.getValue(r.field5()), r.getValue(r.field6()), r.getValue(r.field7())))
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Dto {
        Long a1;
        String a2, a3, a4, a5, a6;
        LocalDateTime a7;
    }
}
