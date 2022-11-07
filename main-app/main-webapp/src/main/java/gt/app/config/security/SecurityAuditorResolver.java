package gt.app.config.security;

import gt.app.domain.User;
import gt.app.modules.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class SecurityAuditorResolver implements AuditorAware<User> {

    private final UserService userService;

    @Override
    public Optional<User> getCurrentAuditor() {
        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();

        return userLogin.map(u -> userService.getReference(userService.findIdByUserLogin(u)));
    }
}
