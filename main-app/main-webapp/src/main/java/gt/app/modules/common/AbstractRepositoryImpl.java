package gt.app.modules.common;

import gt.app.domain.BaseEntity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public abstract class AbstractRepositoryImpl<T extends BaseEntity, R extends AbstractRepository<T>>
    implements AbstractRepositoryCustom<T>, InitializingBean {

    protected R repository;

    public AbstractRepositoryImpl(Class<T> domainClass) {
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(repository, "Repository must not be null.\nAutowire repository with Setter Injection.");
    }


}
