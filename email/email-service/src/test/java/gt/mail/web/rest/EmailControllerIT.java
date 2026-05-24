package gt.mail.web.rest;

import gt.api.email.EmailDto;
import gt.mail.modules.email.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EmailControllerIT {

    @MockitoBean
    EmailService emailService;

    @Test
    void sendEmail(@Autowired MockMvc mvc) throws Exception {
        String body = """
            {
              "fromEmail": "sender@example.com",
              "to": ["test@example.com"],
              "subject": "Test",
              "content": "Hello",
              "isHtml": false,
              "files": []
            }
            """;

        mvc.perform(post("/sendEmail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk());

        verify(emailService).sendEmail(any(EmailDto.class));
    }
}
