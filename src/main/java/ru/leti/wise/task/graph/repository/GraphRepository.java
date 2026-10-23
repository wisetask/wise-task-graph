package ru.leti.wise.task.graph.repository;

import io.micrometer.observation.annotation.Observed;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.leti.wise.task.graph.domain.Graph;

@Observed
public interface GraphRepository extends ReactiveCrudRepository<Graph, String> {
}
