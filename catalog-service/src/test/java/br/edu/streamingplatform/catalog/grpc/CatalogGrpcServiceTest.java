package br.edu.streamingplatform.catalog.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.streamingplatform.catalog.exception.ConteudoNotFoundException;
import br.edu.streamingplatform.catalog.grpc.proto.ContentResponse;
import br.edu.streamingplatform.catalog.grpc.proto.GetContentByIdRequest;
import br.edu.streamingplatform.catalog.model.Conteudo;
import br.edu.streamingplatform.catalog.service.CatalogService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

@ExtendWith(MockitoExtension.class)
class CatalogGrpcServiceTest {

    @Mock
    private CatalogService catalogService;

    private CatalogGrpcService catalogGrpcService;

    @BeforeEach
    void setUp() {
        catalogGrpcService = new CatalogGrpcService(catalogService);
    }

    @Test
    void getContentByIdShouldReturnContentResponse() {
        Conteudo conteudo = new Conteudo("Matrix", "Ficcao cientifica", "Sci-Fi", "MOVIE", 136);
        conteudo.setId(10L);
        when(catalogService.findEntityById(10L)).thenReturn(conteudo);

        RecordingObserver<ContentResponse> observer = new RecordingObserver<>();

        catalogGrpcService.getContentById(
                GetContentByIdRequest.newBuilder().setContentId(10L).build(),
                observer
        );

        assertThat(observer.value).isNotNull();
        assertThat(observer.value.getId()).isEqualTo(10L);
        assertThat(observer.value.getTitle()).isEqualTo("Matrix");
        assertThat(observer.value.getCategory()).isEqualTo("Sci-Fi");
        assertThat(observer.value.getDurationMinutes()).isEqualTo(136);
        assertThat(observer.completed).isTrue();
        assertThat(observer.error).isNull();
    }

    @Test
    void getContentByIdShouldReturnNotFoundWhenContentDoesNotExist() {
        when(catalogService.findEntityById(99L)).thenThrow(new ConteudoNotFoundException(99L));

        RecordingObserver<ContentResponse> observer = new RecordingObserver<>();

        catalogGrpcService.getContentById(
                GetContentByIdRequest.newBuilder().setContentId(99L).build(),
                observer
        );

        assertThat(observer.value).isNull();
        assertThat(observer.completed).isFalse();
        assertThat(Status.fromThrowable(observer.error).getCode()).isEqualTo(Status.Code.NOT_FOUND);
    }

    private static class RecordingObserver<T> implements StreamObserver<T> {

        private T value;
        private Throwable error;
        private boolean completed;

        @Override
        public void onNext(T value) {
            this.value = value;
        }

        @Override
        public void onError(Throwable error) {
            this.error = error;
        }

        @Override
        public void onCompleted() {
            this.completed = true;
        }
    }
}
