package gt.report;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerIT {

    @MockitoBean
    StatReport statReport;

    @Test
    void getFlaggedForReviewCountReturnsCorrectCount(@Autowired MockMvc mvc) throws Exception {
        when(statReport.run()).thenReturn(new StatReport.FlagCount(7));

        mvc.perform(post("/to-review"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.value").value(7));
    }

    @Test
    void getFlaggedForReviewCountReturnsZero(@Autowired MockMvc mvc) throws Exception {
        when(statReport.run()).thenReturn(new StatReport.FlagCount(0));

        mvc.perform(post("/to-review"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.value").value(0));
    }
}
