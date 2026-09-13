package ru.leti.wise.task.graph.domain;

public record Vertex(
        int id,
        Color color,
        Integer weight,
        String label,
        int xCoordinate,
        int yCoordinate
) {
}
