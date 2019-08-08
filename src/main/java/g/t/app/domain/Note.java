package g.t.app.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "note")
@Data
public class Note extends BaseAuditingEntity{

    private String title;

    private String content;

}
