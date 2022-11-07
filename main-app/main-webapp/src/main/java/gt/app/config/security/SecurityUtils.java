package gt.app.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

public final class SecurityUtils {

    public static Optional<Long> getCurrentUserId() {

        User user = getCurrentUserDetails();
        if (user instanceof AppUserDetails appUserDetails) {
            return Optional.of(appUserDetails.getId());
        }
        return Optional.empty();
    }

    public static User getCurrentUserDetails() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return getCurrentUserDetails(authentication);
    }

    public static User getCurrentUserDetails(Authentication authentication) {
        User userDetails = null;
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            userDetails = (User) authentication.getPrincipal();
        }
        return userDetails;
    }
}
