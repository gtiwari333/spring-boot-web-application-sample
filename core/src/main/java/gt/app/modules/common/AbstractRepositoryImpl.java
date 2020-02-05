package gt.app.modules.common;

import com.querydsl.jpa.JPQLQuery;
import gt.app.domain.BaseEntity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractRepositoryImpl<T extends BaseEntity, R extends AbstractRepository<T>>
    extends QuerydslRepositorySupport implements AbstractRepositoryCustom<T>, InitializingBean {

    protected R repository;

    public AbstractRepositoryImpl(Class<T> domainClass) {
        super(domainClass);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(repository, "Repository must not be null.\nAutowire repository with Setter Injection.");
    }

    public Set getSet(JPQLQuery query) {
        return Set.of(query.fetch());
    }

    public boolean exists(JPQLQuery query) {
        return query.fetchCount() > 0L;
    }

    public <T> Optional<T> fetchSingle(JPQLQuery query) {
        List<T> list = query.fetch();
        if (list.isEmpty()) {
            return Optional.empty();
        }

        if (list.size() > 1) {
            throw new RuntimeException("Expecting single result but the query returned more than 1");
        }

        return Optional.of(list.get(0));
    }

    public int count(JPQLQuery query) {
        return (int) query.fetchCount();
    }

    public Page getPage(JPQLQuery<?> query, Pageable pageRequest) {
        List<?> resultList = query
            .offset(pageRequest.getOffset())
            .limit(pageRequest.getPageSize())
            .fetch();

        long totalCount = query.fetchCount();

        return new PageImpl<>(resultList, pageRequest, totalCount);
    }


}
