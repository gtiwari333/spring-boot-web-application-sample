package gt.app.web.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
class UserResourceIT {

    @Test
    void getAccount3xx(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/api/account"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void getAccount5xx(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/api/account"))
            .andExpect(status().isOk())
            .andExpect(authenticated().withRoles("USER").withUsername("user"));
    }

    @Test
    @WithMockUser(value = "user1", authorities = "ROLE_USER")
    void getAccountJson(@Autowired MockMvc mvc) throws Exception {

        mvc.perform(get("/api/account"))
            .andExpect(status().isOk())
            .andExpect(authenticated().withRoles("USER").withUsername("user1"))
            .andExpect(jsonPath("$.authorities").isArray())
            .andExpect(jsonPath("$.authorities[0].authority").value("ROLE_USER"))
            .andExpect(jsonPath("$.username").value("user1"));
    }

    @Test
    void loginAndGetUser(@Autowired MockMvc mvc) throws Exception {

        mvc.perform(formLogin().loginProcessingUrl("/auth/login").user("system").password("pass"))
            .andExpect(authenticated().withUsername("system"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }


}
