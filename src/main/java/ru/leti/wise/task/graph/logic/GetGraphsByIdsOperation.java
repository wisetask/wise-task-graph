package ru.leti.wise.task.graph.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.leti.wise.task.graph.GraphGrpc.GetGraphsByIdsResponse;
import ru.leti.wise.task.graph.GraphGrpc.GraphIds;
import ru.leti.wise.task.graph.mapper.GraphMapper;
import ru.leti.wise.task.graph.repository.GraphRepository;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetGraphsByIdsOperation {

    private final GraphRepository graphRepository;
    private final GraphMapper graphMapper;

    public Mono<GetGraphsByIdsResponse> activate(GraphIds request) {
        var ids = request.getGraphIdsList()
                .stream()
                .filter(id -> !id.isBlank())
                .map(UUID::fromString)
                .toList();
        log.debug("Fetching graphs by ids: requested={}, uniqueIds={}",
                request.getGraphIdsList().size(), ids.size());
        if (ids.isEmpty()) {
            return Mono.just(GetGraphsByIdsResponse.getDefaultInstance());
        }
        return graphRepository.findAllById(ids)
                .collectList()
                .doOnNext(graphs -> log.debug("Graphs fetched by ids: requested={}, found={}", ids.size(), graphs.size()))
                .map(graphs -> GetGraphsByIdsResponse.newBuilder()
                        .addAllGraphs(graphMapper.toGraphs(graphs))
                        .build());
    }
}
