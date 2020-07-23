package gt.app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaAuditing //now @CreatedBy, @LastModifiedBy works
@EnableTransactionManagement
@EnableJpaRepositories(
    repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class,
    basePackages = "gt.app.modules",
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "gt.app.modules.article.search.*")
    }
)
@EnableElasticsearchRepositories(basePackages = {"gt.app.modules.article.search"})
public class JpaConfig {
}
