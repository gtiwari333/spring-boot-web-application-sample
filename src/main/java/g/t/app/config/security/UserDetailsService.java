package g.t.app.config.security;

import g.t.app.domain.User;
import g.t.app.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Optional<User> userFromDatabase = userRepository.findOneWithAuthoritiesByUniqueId(email);

        return userFromDatabase
            .map(this::getCustomUserDetails)
            .orElseThrow(() -> new UsernameNotFoundException(" User with login:" + email + " was not found in the " + " database "));
    }

    @Transactional(readOnly = true)
    public UserDetails getCustomUserDetails(User user) {

        return new UserDetails(user.getId(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getAuthorities(),
            user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked());
    }

}
