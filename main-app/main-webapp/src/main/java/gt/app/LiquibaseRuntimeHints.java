package gt.app;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.stereotype.Component;

@Component
@ImportRuntimeHints(LiquibaseRuntimeHints.class)
public class LiquibaseRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // Register the entire changelog directory recursively
        hints.resources().registerPattern("liquibase/*");
        hints.resources().registerPattern("liquibase/**/*");

        // Liquibase XSD schemas (needed for XML changelog validation)
        hints.resources().registerPattern("www.liquibase.org/xml/ns/dbchangelog/*.xsd");

        // Liquibase internal service files
        hints.resources().registerPattern("META-INF/services/liquibase.*");
        hints.resources().registerPattern("liquibase.build.properties");

        // Reflection hints for Liquibase internals
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
