package ru.leti.wise.task.graph.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.leti.wise.task.graph.GraphGrpc;
import ru.leti.wise.task.graph.repository.GraphRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RemoveGraphOperation {

    private final GraphRepository graphRepository;

    public Mono<GraphGrpc.RemoveGraphResponse> activate(GraphGrpc.RemoveGraphRequest removeGraphRequest) {
        return graphRepository.deleteById(UUID.fromString(removeGraphRequest.getId()))
                .thenReturn(GraphGrpc.RemoveGraphResponse.newBuilder().setId(removeGraphRequest.getId()).build());
    }
}
