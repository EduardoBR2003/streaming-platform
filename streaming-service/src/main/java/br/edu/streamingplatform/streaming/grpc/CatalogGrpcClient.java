package br.edu.streamingplatform.streaming.grpc;

import br.edu.streamingplatform.catalog.grpc.proto.CatalogContentServiceGrpc;
import br.edu.streamingplatform.catalog.grpc.proto.ContentResponse;
import br.edu.streamingplatform.catalog.grpc.proto.GetContentByIdRequest;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CatalogGrpcClient {

    @GrpcClient("catalog-service")
    private CatalogContentServiceGrpc.CatalogContentServiceBlockingStub catalogServiceStub;

    public ContentResponse getContentById(Long contentId) {
        log.info("[gRPC] Querying catalog-service | contentId={}", contentId);

        try {
            GetContentByIdRequest request = GetContentByIdRequest.newBuilder()
                    .setContentId(contentId)
                    .build();

            ContentResponse response = catalogServiceStub.getContentById(request);

            log.info("[gRPC] Response received from catalog-service | title='{}', category='{}'",
                    response.getTitle(), response.getCategory());

            return response;

        } catch (StatusRuntimeException e) {
            log.error("[gRPC] Error calling catalog-service: status={} | message={}",
                    e.getStatus(), e.getMessage());
            throw new RuntimeException("gRPC communication failed with catalog-service: " + e.getStatus().getDescription(), e);
        }
    }
}
