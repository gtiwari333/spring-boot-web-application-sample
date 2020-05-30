package gt.app.modules.user;

import gt.app.config.security.SecurityUtils;
import gt.app.config.security.AppUserDetails;
import gt.app.domain.BaseEntity;
import gt.app.exception.OperationNotAllowedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Slf4j
@RequiredArgsConstructor
@Service("permEvaluator")
public class AppPermissionEvaluatorService implements PermissionEvaluator {

    private final UserAuthorityService userAuthorityService;

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
        User curUser = SecurityUtils.getCurrentUserDetails();

        if (!(curUser instanceof AppUserDetails)) {
            throw new OperationNotAllowedException("Current SecurityContext doesn't have AppUserDetails ");
        }

        AppUserDetails appUser = (AppUserDetails) curUser;

        return userAuthorityService.hasAccess(appUser, id, targetEntity);
    }


}
