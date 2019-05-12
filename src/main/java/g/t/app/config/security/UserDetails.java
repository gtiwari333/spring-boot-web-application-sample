package g.t.app.config.security;

import g.t.app.config.Constants;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class UserDetails extends User {

    private Long id;
    private String firstName;

    private String lastName;

    public boolean isUser() {
        return getGrantedAuthorities().contains(Constants.ROLE_USER);
    }

    public boolean isOwner() {
        return getGrantedAuthorities().contains(Constants.ROLE_OWNER);
    }

    public boolean isSystemAdmin() {
        return getGrantedAuthorities().contains(Constants.ROLE_ADMIN);
    }

    public UserDetails(Long id, String email, String password, String firstName, String lastName, Collection<? extends GrantedAuthority> authorities,
                       boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked) {

        super(email, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Collection<String> getGrantedAuthorities() {
        Collection<GrantedAuthority> authorities = getAuthorities();
        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserDetails that = (UserDetails) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
