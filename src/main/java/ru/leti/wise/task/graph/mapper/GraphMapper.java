package ru.leti.wise.task.graph.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.leti.wise.task.graph.GraphOuterClass;
import ru.leti.wise.task.graph.domain.Color;
import ru.leti.wise.task.graph.domain.Edge;
import ru.leti.wise.task.graph.domain.Graph;
import ru.leti.wise.task.graph.domain.Vertex;

import java.util.List;
import java.util.UUID;


@Mapper(componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface GraphMapper {

    GraphOuterClass.Graph graphToGraphResponse(Graph graph);

    @Mapping(target = "XCoordinate", source = "xCoordinate")
    @Mapping(target = "YCoordinate", source = "yCoordinate")
    GraphOuterClass.Vertex vertexToVertexResponse(Vertex vertex);

    GraphOuterClass.Edge edgeToEdgeResponse(Edge edge);

    Graph graphRequestToGraph(GraphOuterClass.Graph graph);

    List<GraphOuterClass.Graph> toGraphs(List<Graph> graphs);

    @Mapping(target = "xCoordinate", source = "XCoordinate")
    @Mapping(target = "yCoordinate", source = "YCoordinate")
    Vertex vertexRequestToGraph(GraphOuterClass.Vertex vertex);

    Edge edgeRequestToEdge(GraphOuterClass.Edge edge);

    @Mapping(target = "isDirect", source = "graph.direct")
    @Mapping(target = ".", source = "graph")
    GraphOuterClass.Graph commonGraphToGraphResponse(ru.leti.wise.task.graph.model.Graph graph, UUID id);

    GraphOuterClass.Vertex commonVertexToVertexResponse(ru.leti.wise.task.graph.model.Vertex vertex);

    GraphOuterClass.Edge commonEdgeToEdgeResponse(ru.leti.wise.task.graph.model.Edge edge);

    default GraphOuterClass.Color mapColor(Color color) {
        return GraphOuterClass.Color.valueOf(color.name());
    }

    default Color mapColorRequest(GraphOuterClass.Color color) {
        return Color.valueOf(color.name());
    }

    default GraphOuterClass.Color mapCommonColor(ru.leti.wise.task.graph.model.Color color) {
        return GraphOuterClass.Color.valueOf(color.name());
    }
}
