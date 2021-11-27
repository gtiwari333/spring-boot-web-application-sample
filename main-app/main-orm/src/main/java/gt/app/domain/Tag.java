package gt.app.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    @Id
    String name;

    public Tag(String name) {
        this.name = name.toLowerCase();
    }
}
