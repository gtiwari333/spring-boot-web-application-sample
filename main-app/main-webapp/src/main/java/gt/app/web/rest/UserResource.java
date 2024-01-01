package gt.app.web.rest;

import gt.app.config.security.CurrentUserToken;
import gt.app.config.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
class UserResource {

    @GetMapping("/account")
    public CurrentUserToken.UserToken getAccount() {
        var user = SecurityUtils.getCurrentUserDetails();
        if (user != null) {
            user.getUserToken();
        }
        return null;
    }
}
