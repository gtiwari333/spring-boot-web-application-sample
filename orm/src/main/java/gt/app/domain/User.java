package gt.app.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "G_USER") //'user' is reserved keyword in postgres
public class User {

    @Id
    @Type(type = "uuid-char")
    protected UUID id;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private String username; //doesn't need to be unique (multi-tenacy)

    public User() {

    }

    public User(String id, String username, String firstName, String lastName) {
        this.id = UUID.fromString(id);
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
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
