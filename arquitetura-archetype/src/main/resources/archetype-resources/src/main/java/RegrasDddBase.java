package dev.ofernando.arquitetura;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Regras de Ouro da Arquitetura HC.
 * Centraliza a governança para garantir que nenhum módulo quebre o design tático.
 */
public class RegrasDddBase {

    public static ArchRule todasAsRegras(String pacoteBase) {
        return com.tngtech.archunit.lang.CompositeArchRule.of(
            isolamentoDoDominio(pacoteBase),
            fluxoDeDependenciaApplication(pacoteBase),
            regrasDeNomenclaturaRepository(pacoteBase),
            regrasDeNomenclaturaUseCase(pacoteBase),
            isolamentoDaInfraestrutura(pacoteBase)
        );
    }

    // 1. O Domínio é o coração: não pode depender de nada externo (API ou Infra)
    public static ArchRule isolamentoDoDominio(String pacoteBase) {
        return noClasses().that().resideInAPackage(pacoteBase + ".domain..")
            .should().dependOnClassesThat().resideInAnyPackage(pacoteBase + ".api..", pacoteBase + ".infrastructure..");
    }

    // 2. A Camada API só deve falar com Application e Domain
    public static ArchRule fluxoDeDependenciaApi(String pacoteBase) {
        return classes().that().resideInAPackage(pacoteBase + ".api..")
            .should().onlyAccessClassesThat().resideInAnyPackage(
                pacoteBase + ".api..", pacoteBase + ".application..", pacoteBase + ".domain..", 
                "java..", "jakarta..", "io.quarkus..", "io.smallrye..");
    }

    // 3. Application não deve conhecer detalhes de Infraestrutura
    public static ArchRule fluxoDeDependenciaApplication(String pacoteBase) {
        return noClasses().that().resideInAPackage(pacoteBase + ".application..")
            .should().dependOnClassesThat().resideInAPackage(pacoteBase + ".infrastructure..");
    }

    // 4. Repositórios devem ser sempre Interfaces
    public static ArchRule regrasDeNomenclaturaRepository(String pacoteBase) {
        return classes().that().resideInAPackage(pacoteBase + ".domain.repository..")
            .should().beInterfaces()
            .andShould().haveSimpleNameEndingWith("Repository");
    }

    // 5. UseCases devem ter nomenclatura padronizada
    public static ArchRule regrasDeNomenclaturaUseCase(String pacoteBase) {
        return classes().that().resideInAPackage(pacoteBase + ".application.usecase..")
            .should().haveSimpleNameEndingWith("UseCase");
    }

    // 6. Entidades de Domínio não podem ter anotações de persistência (JPA)
    // Elas devem ser POJOs/Records puros. A persistência fica na Infra (PedidoEntity)
    public static ArchRule entidadesPuras(String pacoteBase) {
        return noClasses().that().resideInAPackage(pacoteBase + ".domain.model..")
            .should().beAnnotatedWith("jakarta.persistence.Entity");
    }

    // 7. Implementações de Infra devem estar escondidas nos Adapters
    public static ArchRule isolamentoDaInfraestrutura(String pacoteBase) {
        return classes().that().resideInAPackage(pacoteBase + ".infrastructure.adapter..")
            .should().haveSimpleNameEndingWith("Adapter")
            .orShould().haveSimpleNameEndingWith("RepositoryImpl")
            .orShould().haveSimpleNameEndingWith("Producer")
            .orShould().haveSimpleNameEndingWith("Consumer");
    }

    // 8. Controllers (Resources) devem residir apenas na camada API
    public static ArchRule recursosRestApenasNaApi(String pacoteBase) {
        return classes().that().isAnnotatedWith("jakarta.ws.rs.Path")
            .should().resideInAPackage(pacoteBase + ".api..");
    }

    // 9. Handlers de fila devem estar na Application (Orquestradores)
    public static ArchRule handlersNaApplication(String pacoteBase) {
        return classes().that().resideInAPackage(pacoteBase + ".application.handler..")
            .should().haveSimpleNameEndingWith("Handler");
    }

    // 10. Factories devem residir no Domain Model
    public static ArchRule factoriesNoDominio(String pacoteBase) {
        return classes().that().haveSimpleNameEndingWith("Factory")
            .should().resideInAPackage(pacoteBase + ".domain.model..");
    }
}
