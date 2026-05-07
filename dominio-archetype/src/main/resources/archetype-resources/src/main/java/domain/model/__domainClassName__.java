package ${package}.domain.model;

import java.util.UUID;


// private Email emailContato; // Exemplo de uso do Value Object

/**
 * Agregado Raiz: ${domainClassName}
 * Gerado automaticamente. Implemente as regras de negócio abaixo.
 */
public class ${domainClassName} {
    private UUID id;

    // Construtor protegido: só o pacote domain ou a Factory enxergam
    protected ${domainClassName}(UUID id) {
        this.id = id;
    }

    public void processar() {
        // TODO: Implementar o comportamento principal de ${domainClassName}
        throw new UnsupportedOperationException(
            "⚠️ O domínio '${domainClassName}' foi gerado, mas sua lógica de negócio ainda não foi implementada."
        );
    }

    public void validar() {
        throw new UnsupportedOperationException(
            "⚠️ O método 'validar' de '${domainClassName}' ainda não foi implementado. Implemente as regras de validação aqui."
        );
    }

    public UUID getId() { return id; }
}
