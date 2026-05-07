package ${package}.infrastructure.adapter.inbound.event;

import ${package}.application.handler.${domainClassName}QueueHandler;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class ${domainClassName}Consumer {

    private final ${domainClassName}QueueHandler handler;

    public ${domainClassName}Consumer(${domainClassName}QueueHandler handler) {
        this.handler = handler;
    }

    @Incoming("${artifactId}-in")
    public void consumir(String payload) {
        // Envia para o handler na camada de application
        handler.handle(payload);
    }
}
