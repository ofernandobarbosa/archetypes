package ${package}.domain.model;

import java.util.UUID;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Factory para o Agregado ${domainClassName}.
 * Centraliza a lógica de criação complexa e garante a consistência inicial.
 */
@ApplicationScoped
public class ${domainClassName}Factory {

    /**
     * Cria uma nova instância de ${domainClassName} com as validações iniciais.
     */
    public ${domainClassName} criarNovo(String parametroExemplo) {
        // Gera o ID único (Identidade da Entidade)
        UUID id = UUID.randomUUID();
        
        // Aqui você poderia instanciar Value Objects complexos antes da entidade
        // Ex: Email emailVo = new Email(stringEmail);
        
        ${domainClassName} novaEntidade = new ${domainClassName}(id);
        
        // Configura estados iniciais obrigatórios
        // novaEntidade.setStatus(Status.NOVO);
        
        return novaEntidade;
    }
}
