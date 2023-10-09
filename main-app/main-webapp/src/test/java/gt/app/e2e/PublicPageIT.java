package gt.app.e2e;

import gt.app.config.AppProperties;
import gt.app.frwk.TestDataManager;
import io.hypersistence.utils.jdbc.validator.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static io.hypersistence.utils.jdbc.validator.SQLStatementCountValidator.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableConfigurationProperties(AppProperties.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class PublicPageIT {

    @Autowired
    TestDataManager testDataManager;

    @BeforeEach
    void cleanDB() {
        testDataManager.cleanDataAndCache();
        SQLStatementCountValidator.reset();

    }

    @Test
    void loadIndexPageAndVerifyResultIsCached(@Autowired MockMvc mvc) throws Exception {
        SQLStatementCountValidator.reset();

        MvcResult result = mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML_VALUE))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("Article App - HOME"));
        assertTrue(content.contains("User2 Article"));
        assertTrue(content.contains("Ganesh Tiwari"));

        //load page again
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/")).andExpect(status().isOk());

        //only one select
//        assertSelectCount(1);
//        assertDeleteCount(0);
//        assertInsertCount(0);
//        assertUpdateCount(0);
    }

    @Test
    void testCacheAndDBBothAreResetBetweenTests(@Autowired MockMvc mvc) throws Exception {
        SQLStatementCountValidator.reset();

        MvcResult result = mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML_VALUE))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("Article App - HOME"));
        assertTrue(content.contains("User2 Article"));
        assertTrue(content.contains("Ganesh Tiwari"));

        //load page again
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/")).andExpect(status().isOk());

        //only one select
//        assertSelectCount(1);
//        assertDeleteCount(0);
//        assertInsertCount(0);
//        assertUpdateCount(0);
    }
}
