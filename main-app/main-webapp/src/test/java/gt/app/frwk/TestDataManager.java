package gt.app.frwk;

import gt.app.DataCreator;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TestDataManager implements InitializingBean {
    final EntityManagerFactory entityManagerFactory;
    final DataCreator dataCreator;
    final CacheManager cacheManager;

    @Override
    public void afterPropertiesSet() {
    }

    @Transactional
    public void cleanDataAndCache() {
        cacheManager.getCacheNames().forEach(cn -> {
                System.out.println("Clearing cache for " + cn);
                cacheManager.getCache(cn).clear();
            }
        );

        truncateTables();
        dataCreator.initData();
    }

    public void truncateTables() {
        entityManagerFactory
            .unwrap(SessionFactoryImplementor.class)
            .getSchemaManager()
            .truncateMappedObjects();
    }


}
