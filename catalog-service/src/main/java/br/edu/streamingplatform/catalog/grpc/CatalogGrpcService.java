package br.edu.streamingplatform.catalog.grpc;

import br.edu.streamingplatform.catalog.exception.ConteudoNotFoundException;
import br.edu.streamingplatform.catalog.grpc.proto.CatalogContentServiceGrpc;
import br.edu.streamingplatform.catalog.grpc.proto.ContentResponse;
import br.edu.streamingplatform.catalog.grpc.proto.GetContentByIdRequest;
import br.edu.streamingplatform.catalog.model.Conteudo;
import br.edu.streamingplatform.catalog.service.CatalogService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class CatalogGrpcService extends CatalogContentServiceGrpc.CatalogContentServiceImplBase {

    private final CatalogService catalogService;

    public CatalogGrpcService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    // Atende a chamada gRPC que o streaming-service usara para consultar conteudo.
    @Override
    public void getContentById(GetContentByIdRequest request, StreamObserver<ContentResponse> responseObserver) {
        try {
            Conteudo conteudo = catalogService.findEntityById(request.getContentId());

            // Converte a entidade JPA para a mensagem definida no contrato protobuf.
            ContentResponse response = ContentResponse.newBuilder()
                    .setId(conteudo.getId())
                    .setTitle(conteudo.getTitle())
                    .setDescription(valueOrEmpty(conteudo.getDescription()))
                    .setCategory(conteudo.getCategory())
                    .setType(conteudo.getType())
                    .setDurationMinutes(conteudo.getDurationMinutes())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ConteudoNotFoundException exception) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(exception.getMessage())
                    .asRuntimeException());
        }
    }

    // Protobuf nao aceita null em string, entao campo ausente vira texto vazio.
    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
