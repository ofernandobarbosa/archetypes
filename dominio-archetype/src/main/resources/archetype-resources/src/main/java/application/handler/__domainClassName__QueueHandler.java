package ${package}.application.handler;

import ${package}.application.usecase.Processar${domainClassName}UseCase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ${domainClassName}QueueHandler {

    private final Processar${domainClassName}UseCase useCase;

    public ${domainClassName}QueueHandler(Processar${domainClassName}UseCase useCase) {
        this.useCase = useCase;
    }

    public void handle(String payload) {
        // Lógica para transformar o payload e chamar o useCase
        System.out.println("Processando evento via Kafka: " + payload);
    }
}
