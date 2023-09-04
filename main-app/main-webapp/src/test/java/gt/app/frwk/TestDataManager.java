package gt.app.frwk;

import gt.app.DataCreator;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.metamodel.model.domain.internal.MappingMetamodelImpl;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestDataManager implements InitializingBean {
    final EntityManager em;
    final DataCreator dataCreator;
    final CacheManager cacheManager;

    private Set<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        MappingMetamodelImpl metaModelImpl = (MappingMetamodelImpl) em.getMetamodel();
        tableNames = metaModelImpl
                .entityPersisters()
                .values().stream()
                .map(ep -> ((AbstractEntityPersister) ep).getTableName())
                .collect(Collectors.toSet());

        tableNames.addAll(metaModelImpl
                .collectionPersisters()
                .values().stream()
                .map(ep -> ((AbstractCollectionPersister) ep).getTableName())
                .collect(Collectors.toSet()));
    }

    @Transactional
    public void cleanDataAndCache() {
        cacheManager.getCacheNames().forEach(cn -> cacheManager.getCache(cn).clear());

        truncateTables();
        dataCreator.initData();
    }

    public void truncateTables() {

        //for H2
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        for (String tableName : tableNames) {
            em.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();

//        //for MySQL:
//        em.createNativeQuery("SET @@foreign_key_checks = 0").executeUpdate();
//        for (String tableName : tableNames) {
//            em.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
//        }
//        em.createNativeQuery("SET @@foreign_key_checks = 1").executeUpdate();

    }


}
