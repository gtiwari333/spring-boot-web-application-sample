package g.t.app.service;

import g.t.app.config.security.SecurityUtils;
import g.t.app.config.security.UserDetails;
import g.t.app.domain.BaseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("appSecurity")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserAuthorityService {


    public boolean hasAccess(Long id, String entity) {

        UserDetails curUser = SecurityUtils.getCurrentUserDetails();

        if (curUser.isSystemAdmin()) {
            return true;
        }

        if (curUser.isUser()) {


        }

        return true;
    }


    public boolean hasAccess(BaseEntity entity) {
        return hasAccess(entity.getId(), entity.getClass().getSimpleName());
    }
}
