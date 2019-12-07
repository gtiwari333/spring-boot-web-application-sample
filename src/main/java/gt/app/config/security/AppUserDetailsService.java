package gt.app.config.security;

import gt.app.domain.User;
import gt.app.service.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AppUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Optional<User> userFromDatabase = userService.findWithAuthoritiesByEmail(email);

        return userFromDatabase
            .map(this::getCustomUserDetails)
            .orElseThrow(() -> new UsernameNotFoundException(" User with login:" + email + " was not found in the " + " database "));
    }

    @Transactional(readOnly = true)
    public UserDetails getCustomUserDetails(User user) {

        return new UserDetails(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getAuthorities(),
            user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked());
    }

}
