package ru.leti.wise.task.graph.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.leti.wise.task.graph.GraphGrpc;
import ru.leti.wise.task.graph.GraphGrpc.GetAllGraphsResponse;
import ru.leti.wise.task.graph.domain.Graph;
import ru.leti.wise.task.graph.mapper.GraphMapper;
import ru.leti.wise.task.pagination.Pagination;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetAllGraphsOperation {

    private final ReactiveMongoTemplate mongoTemplate;
    private final GraphMapper graphMapper;

    public Mono<GetAllGraphsResponse> activate(GraphGrpc.GetAllGraphsRequest request) {
        var paginationRequest = request.getPagination();
        var pageable = PageRequest.of(paginationRequest.getPage(), paginationRequest.getPageSize());
        log.debug("Fetching graphs: page={}, pageSize={}", pageable.getPageNumber(), pageable.getPageSize());

        return mongoTemplate.find(buildQuery(request.getFilter()).with(pageable), Graph.class)
                .collectList()
                .zipWith(mongoTemplate.count(buildQuery(request.getFilter()), Graph.class))
                .map(tuple -> buildResponse(tuple.getT1(), tuple.getT2(), pageable));
    }

    private GetAllGraphsResponse buildResponse(List<Graph> graphs, long totalCount, Pageable pageable) {
        var page = new PageImpl<>(graphs, pageable, totalCount);
        log.debug("Graphs fetched: page={}, pageSize={}, returned={}, total={}",
                page.getNumber(), page.getSize(), page.getNumberOfElements(), page.getTotalElements());
        var pagination = Pagination.PaginationResponse.newBuilder()
                .setPage(page.getNumber())
                .setPageSize(page.getSize())
                .setTotalCount(page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .setHasNext(page.hasNext())
                .setHasPrevious(page.hasPrevious())
                .build();
        return GetAllGraphsResponse.newBuilder()
                .addAllItems(graphMapper.toGraphs(graphs))
                .setPagination(pagination)
                .build();
    }

    private Query buildQuery(GraphGrpc.GraphFilter filter) {
        List<Criteria> criteria = new ArrayList<>();
        if (!filter.getId().isBlank())
            criteria.add(Criteria.where("id").is(UUID.fromString(filter.getId())));
        if (!filter.getAuthorId().isBlank())
            criteria.add(Criteria.where("authorId").is(UUID.fromString(filter.getAuthorId())));
        if (filter.getVertexCount() > 0)
            criteria.add(Criteria.where("vertexCount").is(filter.getVertexCount()));
        if (filter.getEdgeCount() > 0)
            criteria.add(Criteria.where("edgeCount").is(filter.getEdgeCount()));
        // is_direct и is_named не помечены в proto как optional,
        // поэтому false трактуется как "фильтр не задан"
        if (filter.getIsDirect())
            criteria.add(Criteria.where("isDirect").is(true));
        if (filter.getIsNamed())
            criteria.add(Criteria.where("isNamed").is(true));
        if (!filter.getName().isBlank())
            criteria.add(Criteria.where("name").regex(Pattern.quote(filter.getName()), "i"));

        var query = new Query();
        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria));
        }
        log.debug("Graph filter built: criteriaCount={}", criteria.size());
        return query;
    }
}
