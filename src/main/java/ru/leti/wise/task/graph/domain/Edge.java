package ru.leti.wise.task.graph.domain;

public record Edge(
        int source,
        int target,
        Color color,
        Integer weight,
        String label
) {
}
