package ${package}.infrastructure.adapter.outbound;

import ${package}.domain.repository.${domainClassName}Repository;
import ${package}.domain.model.${domainClassName};

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ${domainClassName}RepositoryJPA implements ${domainClassName}Repository {

    @Override
    public void salvar(${domainClassName} domain) {
        throw new UnsupportedOperationException(
                "❌ ERRO: A implementação de '${domainClassName}Repository.salvar' precisa ser implementada no módulo domínio-compras.");
    }
}
