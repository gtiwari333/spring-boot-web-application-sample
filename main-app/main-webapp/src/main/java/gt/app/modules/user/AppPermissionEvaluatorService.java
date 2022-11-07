package gt.app.modules.user;

import gt.app.config.security.AppUserDetails;
import gt.app.config.security.SecurityUtils;
import gt.app.domain.BaseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service("permEvaluator")
public class AppPermissionEvaluatorService implements PermissionEvaluator {

    private final UserAuthorityService userAuthorityService;
    private final UserService userService;

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {

        if ((auth == null) || (targetDomainObject == null)) {
            log.warn("Either auth or targetDomainObject null ");
            return false;
        }

        String targetType = targetDomainObject.getClass().getSimpleName();
        Long targetId = ((BaseEntity) targetDomainObject).getId();

        return hasAccess(targetId, targetType);
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
        return hasAccess((Long) targetId, targetType);
    }

    public boolean hasAccess(Long id, String targetEntity) {
        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();

        if (userLogin.isEmpty()) {
            return false;
        }


        return userAuthorityService.hasAccess(new AppUserDetails(userService.findByUserLogin(userLogin.get()), null), id, targetEntity);
    }


}
