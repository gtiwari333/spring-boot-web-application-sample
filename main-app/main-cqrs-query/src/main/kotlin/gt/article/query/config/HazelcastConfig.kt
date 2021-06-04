package gt.article.query.config

import com.hazelcast.config.Config
import com.hazelcast.config.ManagementCenterConfig
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PreDestroy


@Configuration
class HazelcastConfig {

    private val log: Logger = LoggerFactory.getLogger(HazelcastConfig::class.java)

    @Bean
    fun initHazelcast(): HazelcastInstance {

        val instance = Hazelcast.getHazelcastInstanceByName("articleQueryApp")
        if (instance != null) {
            log.debug("Hazelcast already initialized")
            return instance
        }
        val config = Config()
        config.instanceName = "articleQueryApp"
        config.networkConfig.port = 5701
        config.networkConfig.isPortAutoIncrement = true

        config.setManagementCenterConfig(ManagementCenterConfig())

        return Hazelcast.newHazelcastInstance(config)
    }

    @PreDestroy
    fun destroy() {
        log.info("Closing Cache Manager")
        Hazelcast.shutdownAll()
    }
}
