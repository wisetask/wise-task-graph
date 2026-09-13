package ru.leti.wise.task.graph.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.leti.wise.task.graph.GraphGrpc;
import ru.leti.wise.task.graph.GraphGrpc.CreateGraphResponse;
import ru.leti.wise.task.graph.GraphOuterClass;
import ru.leti.wise.task.graph.domain.Graph;
import ru.leti.wise.task.graph.mapper.GraphMapper;
import ru.leti.wise.task.graph.repository.GraphRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateGraphOperation {

    private final GraphRepository graphRepository;
    private final GraphMapper graphMapper;

    public Mono<CreateGraphResponse> activate(GraphGrpc.CreateGraphRequest request) {
        GraphOuterClass.Graph graph = request.getGraph();
        Graph baseGraph = graphMapper.graphRequestToGraph(graph);
        return graphRepository.save(baseGraph)
                .map((__) -> CreateGraphResponse
                        .newBuilder()
                        .setGraph(graph)
                        .build());
    }
}
