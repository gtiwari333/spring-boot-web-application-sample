package gt.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "APP_USER")
public class AppUser  implements Serializable {

    @Serial
    private static final long serialVersionUID = -420530763778423332L;

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    UUID id;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private String username; //doesn't need to be unique (multi-tenacy)


    @Column(nullable = false)
    private String email; //doesn't need to be unique (multi-tenacy)

    public AppUser() {

    }

    public AppUser(UUID id, String username, String firstName, String lastName, String email) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public AppUser(String id, String username, String firstName, String lastName, String email) {
        this(UUID.fromString(id), username, firstName, lastName, email);
    }

    @Override
    public String toString() {
        return "User{" +
            "id='" + id + '\'' +
            "username='" + username + '\'' +
            "firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            '}';
    }
}
