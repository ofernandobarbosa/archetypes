package ${package}.infrastructure.adapter.outbound.event;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class ${domainClassName}Producer {

    @Channel("${artifactId}-out")
    Emitter<String> emitter;

    public void publicarEvento(String msg) {
        emitter.send(msg);
    }
}
