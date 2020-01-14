package gt.app.web.rest;

import gt.app.config.security.SecurityUtils;
import gtapp.jooq.Tables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserResource {
    final DSLContext dsl;

    @GetMapping("/account")
    public User getAccount() {
        log.error("Hello " + dsl.fetch(Tables.AUTHORITY).size());

        return SecurityUtils.getCurrentUserDetails();
    }
}
