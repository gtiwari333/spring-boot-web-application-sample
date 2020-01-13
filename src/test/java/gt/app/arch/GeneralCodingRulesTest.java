package gt.app.arch;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.*;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.conditions.ArchConditions.dependOnClassesThat;
import static com.tngtech.archunit.library.GeneralCodingRules.*;

public class GeneralCodingRulesTest extends ArchitectureTest {


    private static final ArchCondition<JavaClass> USE_PACKAGES_FROM_TRANSITIVE_DEPENDENCIES =
        dependOnClassesThat(resideInAnyPackage("org.unbescape", "org.skyscreamer", "org.h2",
            "com.jcraft", "com.zaxxer", "org.xbill", "io.netty",
            "net.minidev", "org.attoparser", "org.checkerframework", "org.brotli", "org.objenesis",
            "org.opentest4j", "org.rauschig",
            "org.webjars", "org.littleshoot", "org.xmlunit",
            "org.jvnet", "org.mozilla", "antlr"))
            .as("classes from transitive dependencies should not be used");

    private static final ArchCondition<JavaClass> USE_INTERNAL_PACKAGE =
        dependOnClassesThat(resideInAnyPackage("javafx", "java.beans", "java.rmi", "com.oracle", "jdk", "sun", "com.sun"))
            .as("java/sun/oracle internal packages should not be used");

    @Test
    void noClassesShouldUseStandardStreams() {

        ArchRule rule = ArchRuleDefinition.noClasses()
            .should(ACCESS_STANDARD_STREAMS);
        rule.check(classes);
    }

    @Test
    void noClassesShouldThrowGenericExceptions() {
        ArchRule rule = ArchRuleDefinition.noClasses()
            .should(THROW_GENERIC_EXCEPTIONS);
        rule.check(classes);
    }

    @Test
    void noClassesShouldUseStandardLogging() {
        ArchRule rule = ArchRuleDefinition.noClasses()
            .should(USE_JAVA_UTIL_LOGGING);
        rule.check(classes);
    }

    @Test
    void noClassesShouldUseJodaTime() {
        ArchRule rule = ArchRuleDefinition.noClasses()
            .should(USE_JODATIME);

        rule.check(classes);
    }

    @Test
    void noClassesShouldUseInternalPackages() {
        ArchRule rule = ArchRuleDefinition.noClasses()
            .should(USE_INTERNAL_PACKAGE);

        rule.check(classes);
    }

    @Test
    void noClassesShouldUseClassesFromTransitiveDependency() {
        ArchRule rule = ArchRuleDefinition.noClasses()
            .should(USE_PACKAGES_FROM_TRANSITIVE_DEPENDENCIES);

        rule.check(classes);
    }


    @Test
    void noClassShouldUseDateToString() {
        ArchRule rule = ArchRuleDefinition.noClasses()
            .should().callMethod(LocalDate.class, "toString")
            .orShould().callMethod(LocalDateTime.class, "toString")
            .orShould().callMethod(LocalTime.class, "toString")
            .orShould().callMethod(ZonedDateTime.class, "toString")
            .orShould().callMethod(Instant.class, "toString")
            .orShould().callMethod(Date.class, "toString")
            .because("You must use date formatter to format date objects");

        rule.check(classes);
    }

    @Test
    void noClassesShouldHaveMainMethods() {
        ArchRule rule = ArchRuleDefinition.methods()
            .that().areDeclaredInClassesThat().resideInAPackage("gt.app..")
            .and().areDeclaredInClassesThat().doNotHaveFullyQualifiedName("gt.app.Application")
            .and().arePublic()
            .and().areStatic()
            .should().notHaveName("main")
            .because("Write unit tests with assertions instead of writing main classes to test stuff!!!");

        rule.check(classes);
    }
}
