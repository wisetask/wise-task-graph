package ru.leti.wise.task.graph.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.leti.wise.task.graph.GraphGrpc.GenerateGraphRequest;
import ru.leti.wise.task.graph.GraphGrpc.GenerateGraphResponse;
import ru.leti.wise.task.graph.mapper.GraphMapper;
import ru.leti.wise.task.graph.model.Color;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.graph.repository.GraphRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;
import static java.util.concurrent.ThreadLocalRandom.current;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenerateRandomGraphOperation {

    private final GraphMapper graphMapper;
    private final GraphRepository graphRepository;

    private static final int MIN_WEIGHT = 1;
    private static final int MAX_WEIGHT = 15;
    private static final int MIN_COORDINATE = 1;
    private static final int MAX_COORDINATE = 1000;

    public Mono<GenerateGraphResponse> activate(GenerateGraphRequest request) {
        log.debug("Generating random graph: vertexCount={}, edgeCount={}, isDirect={}, isSaved={}",
                request.getVertexCount(), request.getEdgeCount(), request.getIsDirect(), request.getIsSaved());
        var graph = generateGraphResponse(request);
        return request.getIsSaved()
                ? graphRepository.save(graphMapper.graphRequestToGraph(graph.getGraph()))
                .doOnNext(saved -> log.info("Random graph saved: id={}, vertices={}, edges={}, isDirect={}",
                        saved.getId(), saved.getVertexCount(), saved.getEdgeCount(), saved.getIsDirect()))
                .map((__) -> graph)
                : Mono.just(graph)
                .doOnSuccess(generated -> log.debug("Random graph generated (not saved): id={}",
                        generated.getGraph().getId()));
    }

    private GenerateGraphResponse generateGraphResponse(GenerateGraphRequest request) {
        return GenerateGraphResponse.newBuilder()
                .setGraph(graphMapper
                        .commonGraphToGraphResponse(generateGraph(
                                request.getVertexCount(),
                                request.getEdgeCount(),
                                request.getIsDirect()), randomUUID()))
                .build();
    }

    private Graph generateGraph(int vertexCount, int edgeCount, boolean isDirect) {
        return Graph.builder()
                .vertexCount(vertexCount)
                .edgeCount(edgeCount)
                .isDirect(isDirect)
                .vertexList(generateVertexList(vertexCount))
                .edgeList(generateEdgeList(edgeCount, vertexCount, isDirect))
                .build();
    }

    private List<Vertex> generateVertexList(int vertexCount) {
        List<Vertex> result = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            result.add(generateVertex(i));
        }
        return result;
    }

    private List<Edge> generateEdgeList(int edgeCount, int vertexCount, boolean isDirect) {
        int minVertex = 0;
        List<Edge> result = new ArrayList<>();
        for (int i = 0; i < edgeCount; i++) {
            Edge edge = generateEdge(vertexCount, minVertex);
            result.add(edge);
            if (!isDirect) {
                result.add(getReversedEdge(edge));
            }
        }
        return result;
    }

    private Vertex generateVertex(int index) {
        return new Vertex(
                index,
                Color.RED,
                current().nextInt(MIN_WEIGHT, MAX_WEIGHT + 1),
                randomUUID().toString(),
                current().nextInt(MIN_COORDINATE, MAX_COORDINATE + 1),
                current().nextInt(MIN_COORDINATE, MAX_COORDINATE + 1)
        );
    }

    private Edge generateEdge(int vertexCount, int minVertex) {
        return new Edge(
                current().nextInt(minVertex, vertexCount),
                current().nextInt(minVertex, vertexCount),
                Color.RED,
                current().nextInt(MIN_WEIGHT, MAX_WEIGHT + 1),
                randomUUID().toString()
        );
    }

    private Edge getReversedEdge(Edge edge) {
        return new Edge(
                edge.getTarget(),
                edge.getSource(),
                edge.getColor(),
                edge.getWeight(),
                edge.getLabel()
        );
    }
}
