package gt.app.domain;

import lombok.Data;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "topic")
@Data
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)

public class Topic extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

}
