package ${package};

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;

import java.util.Arrays;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Catálogo central de regras de arquitetura DDD/Hexagonal do projeto HC.
 *
 * <p>As constantes (MAIÚSCULAS) são usadas diretamente via {@code @ArchTest} em
 * {@link DddArchTest}. Os métodos que aceitam {@code pacoteBase} ficam disponíveis
 * para uso pontual em testes customizados.
 *
 * <p><b>Pacotes esperados por módulo:</b>
 * <pre>
 *   ${package}.${dominio}.domain..          ← entidades, value objects, repositórios (interfaces)
 *   ${package}.${dominio}.domain.model..    ← aggregates, entities, VOs
 *   ${package}.${dominio}.domain.repository ← interfaces de repositório
 *   ${package}.${dominio}.application..     ← use cases, handlers, serviços de aplicação
 *   ${package}.${dominio}.application.usecase..
 *   ${package}.${dominio}.api..             ← controllers REST (Resources)
 *   ${package}.${dominio}.infrastructure..  ← JPA entities, adapters, produtores
 *   ${package}.${dominio}.infrastructure.adapter..
 * </pre>
 */
public class RegrasDddBase {

    // =====================================================================
    // Constantes para uso com @ArchTest (checam o pacote inteiro analisado)
    // Usadas em DddArchTest via herança
    // =====================================================================

    /** Domínio não pode depender de api ou infrastructure. */
    public static final ArchRule ISOLAMENTO_DO_DOMINIO =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..api..", "..infrastructure..")
            .as("Domínio deve ser isolado: sem dependências de api ou infrastructure");

    /** Application não conhece detalhes de infraestrutura. */
    public static final ArchRule FLUXO_APPLICATION =
        noClasses().that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
            .as("Application não deve conhecer infrastructure");

    /** Repositórios são sempre interfaces com sufixo Repository. */
    public static final ArchRule NOMENCLATURA_REPOSITORY =
        classes().that().resideInAPackage("..domain.repository..")
            .should().beInterfaces()
            .andShould().haveSimpleNameEndingWith("Repository")
            .as("Interfaces de repositório devem terminar com 'Repository'");

    /** Use Cases têm nomenclatura padronizada. */
    public static final ArchRule NOMENCLATURA_USECASE =
        classes().that().resideInAPackage("..application.usecase..")
            .should().haveSimpleNameEndingWith("UseCase")
            .as("Classes em application.usecase devem terminar com 'UseCase'");

    /** Entidades de domínio são POJOs puros — sem @Entity JPA. */
    public static final ArchRule ENTIDADES_PURAS =
        noClasses().that().resideInAPackage("..domain.model..")
            .should().beAnnotatedWith("jakarta.persistence.Entity")
            .as("Entidades de domínio não podem ter @Entity — persistência fica na infrastructure");

    /** Implementações de infrastructure ficam nos adapters com sufixo correto. */
    public static final ArchRule ISOLAMENTO_INFRA =
        classes().that().resideInAPackage("..infrastructure.adapter..")
            .should().haveSimpleNameEndingWith("Adapter")
            .orShould().haveSimpleNameEndingWith("RepositoryImpl")
            .orShould().haveSimpleNameEndingWith("Producer")
            .orShould().haveSimpleNameEndingWith("Consumer")
            .as("Adapters de infrastructure devem terminar com Adapter, RepositoryImpl, Producer ou Consumer");

    /** Controllers REST residem apenas na camada api. */
    public static final ArchRule RECURSOS_REST_NA_API =
        classes().that().areAnnotatedWith("jakarta.ws.rs.Path")
            .should().resideInAPackage("..api..")
            .as("Controllers REST (@Path) devem residir somente na camada api");

    // =====================================================================
    // Métodos para uso pontual em testes customizados (pacote específico)
    // =====================================================================

    public static ArchRule isolamentoDoDominio(String pacoteBase) {
        return noClasses().that().resideInAPackage(pacoteBase + ".domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(pacoteBase + ".api..", pacoteBase + ".infrastructure..");
    }

    public static ArchRule fluxoDeDependenciaApi(String pacoteBase) {
        return classes().that().resideInAPackage(pacoteBase + ".api..")
            .should().onlyAccessClassesThat().resideInAnyPackage(
                pacoteBase + ".api..", pacoteBase + ".application..", pacoteBase + ".domain..",
                "java..", "jakarta..", "io.quarkus..", "io.smallrye..");
    }

    public static ArchRule fluxoDeDependenciaApplication(String pacoteBase) {
        return noClasses().that().resideInAPackage(pacoteBase + ".application..")
            .should().dependOnClassesThat().resideInAPackage(pacoteBase + ".infrastructure..");
    }

    public static ArchRule regrasDeNomenclaturaRepository(String pacoteBase) {
        return classes().that().resideInAPackage(pacoteBase + ".domain.repository..")
            .should().beInterfaces()
            .andShould().haveSimpleNameEndingWith("Repository");
    }

    public static ArchRule regrasDeNomenclaturaUseCase(String pacoteBase) {
        return classes().that().resideInAPackage(pacoteBase + ".application.usecase..")
            .should().haveSimpleNameEndingWith("UseCase");
    }

    public static ArchRule entidadesPuras(String pacoteBase) {
        return noClasses().that().resideInAPackage(pacoteBase + ".domain.model..")
            .should().beAnnotatedWith("jakarta.persistence.Entity");
    }

    public static ArchRule isolamentoDaInfraestrutura(String pacoteBase) {
        return classes().that().resideInAPackage(pacoteBase + ".infrastructure.adapter..")
            .should().haveSimpleNameEndingWith("Adapter")
            .orShould().haveSimpleNameEndingWith("RepositoryImpl")
            .orShould().haveSimpleNameEndingWith("Producer")
            .orShould().haveSimpleNameEndingWith("Consumer");
    }

    public static ArchRule recursosRestApenasNaApi(String pacoteBase) {
        return classes().that().areAnnotatedWith("jakarta.ws.rs.Path")
            .should().resideInAPackage(pacoteBase + ".api..");
    }

    public static ArchRule handlersNaApplication(String pacoteBase) {
        return classes().that().resideInAPackage(pacoteBase + ".application.handler..")
            .should().haveSimpleNameEndingWith("Handler");
    }

    public static ArchRule factoriesNoDominio(String pacoteBase) {
        return classes().that().haveSimpleNameEndingWith("Factory")
            .should().resideInAPackage(pacoteBase + ".domain.model..");
    }

    public static ArchRule todasAsRegras(String pacoteBase) {
        return CompositeArchRule.of(Arrays.asList(
            isolamentoDoDominio(pacoteBase),
            fluxoDeDependenciaApplication(pacoteBase),
            regrasDeNomenclaturaRepository(pacoteBase),
            regrasDeNomenclaturaUseCase(pacoteBase),
            isolamentoDaInfraestrutura(pacoteBase)
        ));
    }
}
