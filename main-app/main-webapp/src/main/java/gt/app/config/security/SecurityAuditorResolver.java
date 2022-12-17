package gt.app.config.security;

import gt.app.domain.AppUser;
import gt.app.modules.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class SecurityAuditorResolver implements AuditorAware<AppUser> {

    private final UserService userService;

    @Override
    public Optional<AppUser> getCurrentAuditor() {
        Optional<Long> userLogin = SecurityUtils.getCurrentUserId();

        return userLogin.map(userService::getReference);
    }
}
