package ru.leti.wise.task.graph.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.leti.wise.task.graph.GraphGrpc;
import ru.leti.wise.task.graph.repository.GraphRepository;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveGraphOperation {

    private final GraphRepository graphRepository;

    public Mono<GraphGrpc.RemoveGraphResponse> activate(GraphGrpc.RemoveGraphRequest removeGraphRequest) {
        var graphId = UUID.fromString(removeGraphRequest.getId());
        log.info("Removing graph: id={}", graphId);
        return graphRepository.deleteById(graphId)
                .doOnSuccess(__ -> log.info("Graph removed: id={}", graphId))
                .thenReturn(GraphGrpc.RemoveGraphResponse.newBuilder().setId(removeGraphRequest.getId()).build());
    }
}
