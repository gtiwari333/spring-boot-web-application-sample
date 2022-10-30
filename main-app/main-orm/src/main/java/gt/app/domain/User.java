package gt.app.domain;

import lombok.Getter;
import lombok.Setter;
//import org.hibernate.annotations.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
@Getter
@Setter
public class User {

    @Id
//    @Type(type = "uuid-char")
    protected UUID id;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private String username; //doesn't need to be unique (multi-tenacy)

    @Column(nullable = false)
    private String email; //doesn't need to be unique (multi-tenacy)

    public User() {

    }

    public User(String id, String username, String firstName, String lastName, String email) {
        this.id = UUID.fromString(id);
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
            "id='" + id + '\'' +
            "username='" + username + '\'' +
            "firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            '}';
    }
}
