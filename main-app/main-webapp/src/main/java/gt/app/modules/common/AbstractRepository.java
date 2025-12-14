package gt.app.modules.common;

import gt.app.domain.BaseEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractRepository<T extends BaseEntity> extends ListCrudRepository<T, Long> {

}
