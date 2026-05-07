package ${package}.application.handler;

import ${package}.application.usecase.Criar${domainClassName}UseCase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ${domainClassName}QueueHandler {

    private final Criar${domainClassName}UseCase useCase;

    public ${domainClassName}QueueHandler(Criar${domainClassName}UseCase useCase) {
        this.useCase = useCase;
    }

    public void handle(String payload) {
        // Lógica para transformar o payload e chamar o useCase
        System.out.println("Processando evento via Kafka: " + payload);
    }
}
