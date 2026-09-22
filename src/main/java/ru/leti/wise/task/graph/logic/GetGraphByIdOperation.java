package ru.leti.wise.task.graph.logic;

import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.leti.wise.task.graph.GraphGrpc;
import ru.leti.wise.task.graph.GraphGrpc.GetGraphByIdResponse;
import ru.leti.wise.task.graph.domain.Graph;
import ru.leti.wise.task.graph.mapper.GraphMapper;
import ru.leti.wise.task.graph.repository.GraphRepository;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetGraphByIdOperation {

    private final GraphMapper graphMapper;
    private final GraphRepository graphRepository;

    public Mono<GetGraphByIdResponse> activate(GraphGrpc.GetGraphByIdRequest request) {
        var graphId = UUID.fromString(request.getId());
        log.debug("Fetching graph: id={}", graphId);
        return graphRepository.findById(graphId)
                .doOnNext(graph -> log.debug("Graph found: id={}, name={}, named={}, vertices={}, edges={}",
                        graph.getId(), graph.getName(), graph.getIsNamed(),
                        graph.getVertexCount(), graph.getEdgeCount()))
                .map(this::createResponse)
                .switchIfEmpty(Mono.defer(() -> Mono.error(graphNotFound(graphId))));
    }

    private RuntimeException graphNotFound(UUID graphId) {
        log.warn("Graph not found: id={}", graphId);
        return Status.NOT_FOUND
                .withDescription("Граф с id '%s' не найден".formatted(graphId))
                .asRuntimeException();
    }

    private GetGraphByIdResponse createResponse(Graph graph) {
        return GetGraphByIdResponse
                .newBuilder()
                .setGraph(graphMapper.graphToGraphResponse(graph))
                .build();
    }
}
