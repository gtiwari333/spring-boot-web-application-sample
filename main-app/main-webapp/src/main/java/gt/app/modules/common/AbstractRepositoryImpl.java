package gt.app.modules.common;

import com.querydsl.jpa.JPQLQuery;
import gt.app.domain.BaseEntity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.Assert;

public abstract class AbstractRepositoryImpl<T extends BaseEntity, R extends AbstractRepository<T>>
    extends QuerydslRepositorySupport implements AbstractRepositoryCustom<T>, InitializingBean {

    protected R repository;

    public AbstractRepositoryImpl(Class<T> domainClass) {
        super(domainClass);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(repository, "Repository must not be null.\nAutowire repository with Setter Injection.");
    }

    public boolean exists(JPQLQuery query) {
        return query.fetchCount() > 0L;
    }

}
