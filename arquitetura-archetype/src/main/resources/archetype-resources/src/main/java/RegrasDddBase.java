package ${package};

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class RegrasDddBase {

    public static ArchRule regras(String pacoteBase) {
        return classes().that().resideInAPackage(pacoteBase + ".domain..")
            .should().onlyBeAccessed().byAnyPackage(pacoteBase + ".domain..", pacoteBase + ".application..", pacoteBase + ".infrastructure..");
    }

    public static ArchRule isolamentoDaApi(String pacoteBase) {
        return noClasses().that().resideInAPackage(pacoteBase + ".domain..")
            .should().dependOnClassesThat().resideInAPackage(pacoteBase + ".api..");
    }
}
