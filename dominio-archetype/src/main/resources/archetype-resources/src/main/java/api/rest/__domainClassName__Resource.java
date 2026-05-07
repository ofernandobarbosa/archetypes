package ${package}.api.rest;

import ${package}.application.usecase.Criar${domainClassName}UseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Camada de API (Quarkus Resource): Contrato REST usando JAX-RS.
 */
@Path("/v1/${artifactId}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ${domainClassName}Resource {

    private final Criar${domainClassName}UseCase criar${domainClassName}UseCase;

    @Inject
    public ${domainClassName}Resource(Criar${domainClassName}UseCase criar${domainClassName}UseCase) {
        this.criar${domainClassName}UseCase = criar${domainClassName}UseCase;
    }

    @POST
    public Response criar(Object requestDto) {
        // TODO: Substituir 'Object' por um DTO (Record Java 17+)
        // TODO: Mapear DTO -> Domínio
        
        criar${domainClassName}UseCase.executar(null); // TODO: Substituir 'null' pelo domínio mapeado
        
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/status")
    public String getStatus() {
        return "O domínio '${domainClassName}' está operando no Quarkus.";
    }
}