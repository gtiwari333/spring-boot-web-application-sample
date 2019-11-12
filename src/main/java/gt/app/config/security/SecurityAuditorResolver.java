package gt.app.config.security;

import gt.app.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityAuditorResolver implements AuditorAware<User> {

    private final EntityManager entityManager;

    @Override
    public Optional<User> getCurrentAuditor() {

        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(entityManager.getReference(User.class, userId));
    }
}
