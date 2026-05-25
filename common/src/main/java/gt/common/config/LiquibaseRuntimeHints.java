package gt.common.config;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class LiquibaseRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerPattern("liquibase/*");
        hints.resources().registerPattern("liquibase/**/*");
        hints.resources().registerPattern("www.liquibase.org/xml/ns/dbchangelog/*.xsd");
        hints.resources().registerPattern("META-INF/services/liquibase.*");
        hints.resources().registerPattern("liquibase.build.properties");

        hints.reflection().registerType(
            liquibase.changelog.ChangeLogHistoryServiceFactory.class,
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
        );
        hints.reflection().registerType(
            liquibase.database.DatabaseFactory.class,
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
            MemberCategory.INVOKE_DECLARED_METHODS
        );
        hints.reflection().registerType(
            liquibase.parser.ChangeLogParserFactory.class,
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
        );
    }
}
