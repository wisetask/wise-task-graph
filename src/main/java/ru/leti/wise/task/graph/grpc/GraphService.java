package ru.leti.wise.task.graph.grpc;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import ru.leti.wise.task.graph.GraphGrpc.*;
import ru.leti.wise.task.graph.GraphServiceGrpc;
import ru.leti.wise.task.graph.logic.*;
import ru.leti.wise.task.graph.util.LogInterceptor;

@Slf4j
@Observed
@GrpcService(interceptors = {LogInterceptor.class})
@RequiredArgsConstructor
public class GraphService extends GraphServiceGrpc.GraphServiceImplBase {

    private final GetGraphByIdOperation getGraphByIdOperation;
    private final CreateGraphOperation createGraphOperation;
    private final GenerateRandomGraphOperation generateRandomGraphOperation;
    private final GetGraphLibraryOperation getGraphLibraryOperation;
    private final RemoveGraphOperation removeGraphOperation;

    @Override
    public void getGraphById(GetGraphByIdRequest request,
                             StreamObserver<GetGraphByIdResponse> responseObserver) {
        getGraphByIdOperation
                .activate(request)
                .subscribe(
                        responseObserver::onNext,
                        responseObserver::onError,
                        responseObserver::onCompleted
                );
    }

    @Override
    public void createGraph(CreateGraphRequest request,
                            StreamObserver<CreateGraphResponse> responseObserver) {
        createGraphOperation
                .activate(request)
                .subscribe(
                        responseObserver::onNext,
                        responseObserver::onError,
                        responseObserver::onCompleted
                );
    }

    @Override
    public void generateRandomGraph(GenerateGraphRequest request,
                                    StreamObserver<GenerateGraphResponse> responseObserver) {
        generateRandomGraphOperation
                .activate(request)
                .subscribe(
                        responseObserver::onNext,
                        responseObserver::onError,
                        responseObserver::onCompleted
                );

    }

    @Override
    public void getGraphLibrary(Empty request,
                                StreamObserver<GetGraphLibraryResponse> responseObserver) {
        getGraphLibraryOperation
                .activate()
                .subscribe(
                        responseObserver::onNext,
                        responseObserver::onError,
                        responseObserver::onCompleted
                );

    }

    @Override
    public void removeGraph(RemoveGraphRequest request,
                            StreamObserver<RemoveGraphResponse> responseObserver) {
        removeGraphOperation
                .activate(request)
                .subscribe(
                        responseObserver::onNext,
                        responseObserver::onError,
                        responseObserver::onCompleted
                );
    }
}
