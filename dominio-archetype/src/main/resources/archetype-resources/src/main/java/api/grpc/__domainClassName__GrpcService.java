package ${package}.api.grpc;

import ${package}.application.usecase.Processar${domainClassName}UseCase;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;

@GrpcService
public class ${domainClassName}GrpcService implements ${domainClassName}Service { // Interface gerada pelo proto

    private final Processar${domainClassName}UseCase useCase;

    public ${domainClassName}GrpcService(Processar${domainClassName}UseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public Uni<${domainClassName}Reply> enviar( ${domainClassName}Request request) {
        // Mapeia o Request do Proto para o Domínio e executa o UseCase
        return Uni.createFrom().item(() -> {
            useCase.executar(null); // Passar objeto mapeado
            return ${domainClassName}Reply.newBuilder().setMessage("Sucesso").build();
        });
    }
}
