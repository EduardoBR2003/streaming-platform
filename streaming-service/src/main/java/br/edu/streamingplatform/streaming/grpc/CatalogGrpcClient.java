package br.edu.streamingplatform.streaming.grpc;

import com.streaming.grpc.CatalogServiceGrpc;
import com.streaming.grpc.ContentRequest;
import com.streaming.grpc.ContentResponse;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CatalogGrpcClient {

    @GrpcClient("catalog-service")
    private CatalogServiceGrpc.CatalogServiceBlockingStub catalogServiceStub;

    public ContentResponse getContentById(Long contentId) {
        log.info("[gRPC] Querying catalog-service | contentId={}", contentId);

        try {
            ContentRequest request = ContentRequest.newBuilder()
                    .setContentId(contentId)
                    .build();

            ContentResponse response = catalogServiceStub.getContent(request);

            log.info("[gRPC] Response received from catalog-service | found={}, title='{}', category='{}'",
                    response.getFound(), response.getTitle(), response.getCategory());

            return response;

        } catch (StatusRuntimeException e) {
            log.error("[gRPC] Error calling catalog-service: status={} | message={}",
                    e.getStatus(), e.getMessage());
            throw new RuntimeException("gRPC communication failed with catalog-service: " + e.getStatus().getDescription(), e);
        }
    }
}
