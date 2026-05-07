package ${package}.api.grpc;

import ${package}.application.usecase.Criar${domainClassName}UseCase;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;

@GrpcService
public class ${domainClassName}GrpcService implements ${domainClassName}Service { // Interface gerada pelo proto

    private final Criar${domainClassName}UseCase useCase;

    public ${domainClassName}GrpcService(Criar${domainClassName}UseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    // aqui usamos a interface ComprasService gerada pelo .proto
    public Uni<${domainClassName}Response> enviarDados( ${domainClassName}Request request) {
        // Mapeia o Request do Proto para o Domínio e executa o UseCase
        return Uni.createFrom().item(() -> {
            useCase.executar(null); // Passar objeto mapeado
            // dados set dependem do Response do proto, aqui é apenas um exemplo
            return ${domainClassName}Response.newBuilder().setMensagem("Sucesso").setSucesso(true).build();
        });
    }
}
