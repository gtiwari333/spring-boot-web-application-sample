package gt.app.domain;

import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class User extends BaseEntity implements UserDetails {
    @Basic(fetch = FetchType.LAZY)
    @Lob
    byte[] avatar;
    @Column(nullable = false)
    private String firstName;
    private String lastName;
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    @Column(length = 254, unique = true, nullable = false)
    private String email;
    @Column(nullable = false, unique = true)
    @Size(min = 5, max = 20)
    private String uniqueId;
    @Column(name = "password_hash", length = 60)
    private String password;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_authority",
        joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
    private Set<Authority> authorities = new HashSet<>();
    /*
     * TODO: BLOGIT http://stackoverflow.com/questions/3383169/hibernate-jpa-mysql-and- tinyint1-for-boolean-instead-of-bit-or-char
     */
    @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    private boolean active = false;
    @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    private boolean accountNonExpired;
    @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    private boolean accountNonLocked;
    @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    private boolean credentialsNonExpired;
    private String activationKey;
    private String resetKey;

    @Override
    public Collection<Authority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return uniqueId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    public User(){

    }
    public User(String uniqueId, LocalDate dob, String firstName, String lastName, String email) {
        this.uniqueId = uniqueId;
        this.dateOfBirth = dob;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.active = true;
    }

    @Override
    public String toString() {
        return "User{" +
            "firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", dateOfBirth=" + dateOfBirth +
            ", email='" + email + '\'' +
            ", authorities=" + authorities +
            ", active=" + active +
            ", accountNonExpired=" + accountNonExpired +
            ", accountNonLocked=" + accountNonLocked +
            ", credentialsNonExpired=" + credentialsNonExpired +
            '}';
    }
}
