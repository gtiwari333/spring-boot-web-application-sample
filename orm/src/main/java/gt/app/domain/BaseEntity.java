package gt.app.domain;

import gt.app.domain.customtype.PersistentEnumType;
import lombok.Data;
import org.hibernate.annotations.TypeDef;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
@TypeDef(name = "persistentEnum", typeClass = PersistentEnumType.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
}
