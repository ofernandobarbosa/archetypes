package ${package}.domain.model.vo;

import java.io.Serializable;

/**
 * Exemplo de Value Object (VO).
 * Value Objects são imutáveis e definidos pelo seu valor, não por um ID.
 */
public record Email(String endereco) implements Serializable {

    public Email {
        // Validação: Um VO nunca deve ser criado em estado inválido
        if (endereco == null || !endereco.contains("@")) {
            throw new IllegalArgumentException("Formato de e-mail inválido.");
        }
    }

    // VOs podem ter métodos de negócio auxiliares
    public String getDominio() {
        return endereco.substring(endereco.indexOf("@") + 1);
    }
    
    @Override
    public String toString() {
        return endereco;
    }
}
