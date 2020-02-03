package gt.app.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "topic")
@Data
public class Topic extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

}
