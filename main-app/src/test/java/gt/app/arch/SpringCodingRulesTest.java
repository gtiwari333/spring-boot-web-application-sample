package gt.app.arch;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;

public class SpringCodingRulesTest extends ArchitectureTest {

    @Test
    void springSingletonComponentsShouldOnlyHaveFinalFields() {
        ArchRule rule = ArchRuleDefinition.classes()
            .that().areAnnotatedWith(Service.class)
            .or().areAnnotatedWith(Component.class)
            .or().areAnnotatedWith(ConfigurationProperties.class)
            .or().areAnnotatedWith(Controller.class)
            .or().areAnnotatedWith(RestController.class)
            .or().areAnnotatedWith(Repository.class)
            .should().haveOnlyFinalFields();

        rule.check(classes);
    }

    @Test
    void springFieldDependencyInjectionShouldNotBeUsed() {
        ArchRule rule = ArchRuleDefinition.noFields()
            .should().beAnnotatedWith(Autowired.class);

        rule.check(classes);
    }

    @Test
    void springComponentInteraction() {

        ArchRule rule =
            ArchRuleDefinition.classes()
                .that().resideInAPackage("..service..")
                .should().onlyBeAccessed()
                .byAnyPackage("..service..",
                    "..web..",
                    "..security..",
                    "..config.."
                );

        rule.check(classes);
    }


    @Test
    void springRepositoryInteraction() {

        ArchRule rule =
            ArchRuleDefinition.classes()
                .that().resideInAPackage("..repository..")
                .should().onlyBeAccessed()
                .byAnyPackage(
                    "..modules.."
                );

        rule.check(classes);
    }

    @Test
    void domainClassesShouldOnlyDependOnDomainOrStdLibClasses() {
        ArchRule rule = ArchRuleDefinition.classes()
            .that().resideInAPackage(DOMAIN_LAYER_PACKAGES)
            .and().areAnnotatedWith(Entity.class)
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                DOMAIN_LAYER_PACKAGES, "java..", "lombok..", "javax..", "",
                "com.fasterxml.jackson..", "org.hibernate.annotations",
                "org.apache.commons.lang3..", "org.springframework.security.core.."
            );
        rule.check(classes);
    }

    @Test
    void controllerClassesShouldBeAnnotatedWithControllerOrRestControllerAnnotation() {
        ArchRule rule = ArchRuleDefinition.classes()
            .that().haveSimpleNameEndingWith("Controller")
            .or().haveSimpleNameEndingWith("Resource")
            .should().beAnnotatedWith(Controller.class)
            .orShould().beAnnotatedWith(RestController.class);

        rule.check(classes);
    }

    @Test
    void noClassesWithControllerOrRestControllerAnnotationShouldResideOutsideOfPrimaryAdaptersPackages() {
        ArchRule rule = ArchRuleDefinition.noClasses()
            .that().areAnnotatedWith(Controller.class)
            .or().areAnnotatedWith(RestController.class)
            .should().resideOutsideOfPackage(WEB_LAYER_CLASSES);

        rule.check(classes);
    }

    @Test
    void controllerClassesShouldNotDependOnEachOther() {
        ArchRule rule = ArchRuleDefinition.noClasses()
            .that().areAnnotatedWith(Controller.class)
            .or().areAnnotatedWith(RestController.class)
            .should().dependOnClassesThat()
            .areAnnotatedWith(Controller.class);

        rule.check(classes);
    }


    @Test
    void controllerClassesShouldNotDependOnEachOther2() {
        ArchRule rule = ArchRuleDefinition.noClasses()
            .that().areAnnotatedWith(Controller.class)
            .or().areAnnotatedWith(RestController.class)
            .should().dependOnClassesThat()
            .areAnnotatedWith(RestController.class);

        rule.check(classes);
    }

    @Test
    void publicRestControllerMethodsShouldBeAnnotatedWithARequestMapping() {
        ArchRule rule = ArchRuleDefinition.methods()
            .that().arePublic()
            .and().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
            .should().beAnnotatedWith(RequestMapping.class)
            .orShould().beAnnotatedWith(GetMapping.class)
            .orShould().beAnnotatedWith(PostMapping.class)
            .orShould().beAnnotatedWith(PutMapping.class)
            .orShould().beAnnotatedWith(DeleteMapping.class);

        rule.check(classes);
    }

    @Test
    void publicControllerMethodsShouldBeAnnotatedWithARequestMapping() {
        ArchRule rule = ArchRuleDefinition.methods()
            .that().arePublic()
            .and().areDeclaredInClassesThat().areAnnotatedWith(Controller.class)
            .should().beAnnotatedWith(RequestMapping.class)
            .orShould().beAnnotatedWith(GetMapping.class)
            .orShould().beAnnotatedWith(PostMapping.class)
            .orShould().beAnnotatedWith(PutMapping.class)
            .orShould().beAnnotatedWith(DeleteMapping.class);

        rule.check(classes);
    }


    @Test
    void noClassShouldHaveMethodAnnotatedWithPostConstruct() {
        ArchRule rule = ArchRuleDefinition.methods()
            .should().notBeAnnotatedWith(PostConstruct.class)
            .because("You need to implement InitializingBean and move your @PostConstruct logic into afterPropertiesSet() so that Spring handles bean initialization better ");

        rule.check(classes);
    }


    @Test
    void noClassesWithEntityAnnotationShouldResideOutsideOfDomainPackage() {
        ArchRule rule = ArchRuleDefinition.noClasses()
            .that().areAnnotatedWith(Entity.class)
            .should().resideOutsideOfPackage(DOMAIN_LAYER_PACKAGES);
        rule.check(classes);
    }


    @Test
    void serviceImplClassesShouldBeAnnotatedWithServiceAnnotation() {
        ArchRule rule = ArchRuleDefinition.classes()
            .that().haveSimpleNameEndingWith("ServiceImpl")
            .should().beAnnotatedWith(Service.class);

        rule.check(classes);
    }

    @Test
    void serviceClassesShouldBeAnnotatedWithServiceAnnotation() {
        ArchRule rule = ArchRuleDefinition.classes()
            .that().haveSimpleNameEndingWith("Service")
            .and().areNotInterfaces()
            .should().beAnnotatedWith(Service.class);

        rule.check(classes);
    }

    @Test
    void noServiceClassesShouldResideOutsideDesignatedPackages() {
        ArchRule rule = ArchRuleDefinition.noClasses()
            .that().haveSimpleNameEndingWith("Service")
            .or().areAnnotatedWith(Service.class)
            .should().resideOutsideOfPackages(SERVICE_LAYER_PACKAGES);

        rule.check(classes);

    }


    @Test
    void noControllerClassesShouldResideOutsideDesignatedPackages() {
        ArchRule rule = ArchRuleDefinition.noClasses()
            .that().haveSimpleNameEndingWith("Controller")
            .or().areAnnotatedWith(Controller.class)
            .or().areAnnotatedWith(RestController.class)
            .should().resideOutsideOfPackages(WEB_LAYER_CLASSES);

        rule.check(classes);

    }

    @Test
    void noClassesWithConfigurationAnnotationShouldResideOutsideOfConfigPackages() {
        ArchRule rule = ArchRuleDefinition.noClasses()
            .that().areAnnotatedWith(Configuration.class)
            .should().resideOutsideOfPackage(CONFIG_PACKAGE);
        rule.check(classes);
    }


}
