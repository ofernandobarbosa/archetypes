package ${package}.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import ${package}.domain.model.${domainClassName};
import ${package}.domain.repository.${domainClassName}Repository;

@ApplicationScoped
public class Criar${domainClassName}UseCase {

    private final ${domainClassName}Repository repository;

    public Criar${domainClassName}UseCase(${domainClassName}Repository repository) {
        this.repository = repository;
    }

    public void executar(${domainClassName} domain) {
        // 1. Regra de Negócio via Domínio
        domain.validar();
        // 2. Persistência via Porta de Saída
        repository.salvar(domain);
        throw new UnsupportedOperationException(
                    "❌ ERRO: O Caso de Uso 'Criar${domainClassName}UseCase' precisa de implementação."
                );
    }
}