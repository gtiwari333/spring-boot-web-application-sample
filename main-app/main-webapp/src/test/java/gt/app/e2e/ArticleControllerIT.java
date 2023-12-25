package gt.app.e2e;

import gt.app.DataCreator;
import gt.app.modules.user.AppUserDetailsService;
import io.hypersistence.utils.jdbc.validator.SQLStatementCountValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static io.hypersistence.utils.jdbc.validator.SQLStatementCountValidator.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ArticleControllerIT {

    @Autowired
    AppUserDetailsService userDetailsService;

    @Test
    void testUserPage(@Autowired MockMvc mvc) throws Exception {

        //pull real data to mock user login with real userDetails object
        var user1 = userDetailsService.loadUserByUsername(DataCreator.USER1);
        var user2 = userDetailsService.loadUserByUsername(DataCreator.USER2);
        var systemUser = userDetailsService.loadUserByUsername(DataCreator.SYSTEM_USER);

        SQLStatementCountValidator.reset();

        mvc.perform(get("/article").with(user(user1))).andExpect(status().isOk()); //1 query
        mvc.perform(get("/article").with(user(user2))).andExpect(status().isOk()); //1 query
        mvc.perform(get("/article").with(user(systemUser))).andExpect(status().isOk()); //1 query

        //load page again
        mvc.perform(get("/article").with(user(user1))).andExpect(status().isOk()); //0 query
        mvc.perform(get("/article").with(user(user1))).andExpect(status().isOk()); //0 query
        mvc.perform(get("/article").with(user(user2))).andExpect(status().isOk()); //0 query
        mvc.perform(get("/article").with(user(user2))).andExpect(status().isOk()); //0 query
        mvc.perform(get("/article").with(user(systemUser))).andExpect(status().isOk()); //0 query
        mvc.perform(get("/article").with(user(systemUser))).andExpect(status().isOk()); //0 query

        mvc.perform(get("/article/read/1").with(user(user1))).andExpect(status().isOk()); //1 query
        mvc.perform(get("/article/read/1").with(user(user2))).andExpect(status().isOk()); //0 query
        mvc.perform(get("/article/read/1").with(user(systemUser))).andExpect(status().isOk()); //0 query

        //only one select
//        assertSelectCount(4);
//        assertDeleteCount(0);
//        assertInsertCount(0);
//        assertUpdateCount(0);
    }

}
