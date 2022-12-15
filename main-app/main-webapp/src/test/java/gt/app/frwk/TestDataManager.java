package gt.app.frwk;

import gt.app.DataCreator;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.metamodel.model.domain.internal.MappingMetamodelImpl;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestDataManager implements InitializingBean {
    final EntityManager em;
    final DataCreator dataCreator;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        MappingMetamodelImpl metaModelImpl = (MappingMetamodelImpl) em.getMetamodel();
        tableNames = metaModelImpl
            .entityPersisters()
            .values().stream()
            .map(ep -> ((AbstractEntityPersister) ep).getTableName())
            .collect(Collectors.toList());

    }

    @Transactional
    public void truncateTablesAndRecreate() {
        truncateTables();
        dataCreator.initData();
    }

    protected void truncateTables() {

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
