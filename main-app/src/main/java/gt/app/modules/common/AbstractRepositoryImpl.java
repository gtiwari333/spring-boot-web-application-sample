package gt.app.modules.common;

import com.querydsl.jpa.JPQLQuery;
import gt.app.domain.BaseEntity;
import org.jooq.SortField;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.*;

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
            throw new BadQueryException("Expecting single result but the query returned more than 1");
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


    protected Collection<SortField<?>> getSortFields(Sort sortSpecification, TableImpl JOOQ_TBL) {

        if (sortSpecification == null || sortSpecification.isEmpty()) {
            return List.of();
        }

        Iterator<Sort.Order> specifiedFields = sortSpecification.iterator();
        Collection<SortField<?>> querySortFields = new ArrayList<>();

        while (specifiedFields.hasNext()) {
            Sort.Order specifiedField = specifiedFields.next();

            String sortFieldName = specifiedField.getProperty();
            Sort.Direction sortDirection = specifiedField.getDirection();

            TableField tableField = getTableField(sortFieldName, JOOQ_TBL);
            SortField<?> querySortField = convertTableFieldToSortField(tableField, sortDirection);
            querySortFields.add(querySortField);
        }

        return querySortFields;
    }

    private TableField getTableField(String sortFieldName, TableImpl JOOQ_TBL) {
        TableField sortField = null;
        try {
            Field fld = JOOQ_TBL.getClass().getField(sortFieldName);
            sortField = (TableField) fld.get(JOOQ_TBL);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            String errorMessage = String.format("Could not find table field: {}", sortFieldName);
            throw new InvalidDataAccessApiUsageException(errorMessage, ex);
        }

        return sortField;
    }

    private SortField<?> convertTableFieldToSortField(TableField tableField, Sort.Direction sortDirection) {
        if (sortDirection == Sort.Direction.ASC) {
            return tableField.asc();
        } else {
            return tableField.desc();
        }
    }


}
