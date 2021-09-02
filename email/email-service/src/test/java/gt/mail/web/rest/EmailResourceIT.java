package gt.mail.web.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
class EmailResourceIT {

    @Test
    void sayHello2(@Autowired MockMvc mvc) throws Exception {

        mvc.perform(
            post("/sendEmail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(sampleEmail().getBytes()))
            .andExpect(status().isOk());
    }

    String sampleEmail() {
        return "{\"from\":\"test@email.com\",\"subject\":\"Test\",\"content\":\"Body\",\"isHtml\":false,\"files\":[],\"to\":[\"recep@emai.com\"],\"cc\":[],\"bcc\":[]}";
    }
}
