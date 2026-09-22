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
        log.info("Creating graph: id={}, authorId={}, name={}, named={}, vertices={}, edges={}",
                graph.getId(), graph.getAuthorId(), graph.getName(), graph.getIsNamed(),
                graph.getVertexListCount(), graph.getEdgeListCount());
        return graphRepository.save(baseGraph)
                .doOnNext(saved -> log.info("Graph saved: id={}, name={}, named={}",
                        saved.getId(), saved.getName(), saved.getIsNamed()))
                .map((__) -> CreateGraphResponse
                        .newBuilder()
                        .setGraph(graph)
                        .build());
    }
}
