package ru.leti.wise.task.graph.logic;

import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.leti.wise.task.graph.GraphGrpc;
import ru.leti.wise.task.graph.GraphGrpc.GetGraphByIdResponse;
import ru.leti.wise.task.graph.domain.Graph;
import ru.leti.wise.task.graph.mapper.GraphMapper;
import ru.leti.wise.task.graph.repository.GraphRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetGraphByIdOperation {

    private final GraphMapper graphMapper;
    private final GraphRepository graphRepository;

    public Mono<GetGraphByIdResponse> activate(GraphGrpc.GetGraphByIdRequest request) {
        var graphId = UUID.fromString(request.getId());
        return graphRepository.findById(graphId)
                .map(this::createResponse)
                .switchIfEmpty(Mono.error(
                                Status.NOT_FOUND
                                        .withDescription("Граф с id '%s' не найден".formatted(graphId))
                                        .asRuntimeException()
                        )
                );
    }

    private GetGraphByIdResponse createResponse(Graph graph) {
        return GetGraphByIdResponse
                .newBuilder()
                .setGraph(graphMapper.graphToGraphResponse(graph))
                .build();
    }
}
