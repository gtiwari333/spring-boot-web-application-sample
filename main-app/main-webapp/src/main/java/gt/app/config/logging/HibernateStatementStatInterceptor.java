package gt.app.config.logging;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.io.Serial;
import java.io.Serializable;

@Slf4j
public class HibernateStatementStatInterceptor implements StatementInspector, Serializable {

    @Serial
    private static final long serialVersionUID = -7875557911815131906L;
    private final transient ThreadLocal<Long> queryCount = new ThreadLocal<>();

    public void startCounter() {
        queryCount.set(0L);
    }

    public Long getQueryCount() {
        return queryCount.get();
    }

    public void clearCounter() {
        queryCount.remove();
    }

    @Override
    public String inspect(String sql) {
        Long count = queryCount.get();
        if (count != null) {
            queryCount.set(count + 1);
        }

        return null;
    }
}
