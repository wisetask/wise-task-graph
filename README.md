# wise-task-graph

Микросервис хранения и генерации графов. Отвечает за CRUD графов, генерацию случайных графов и поиск по фильтрам. Данные хранит в MongoDB.

## Технологии

- Java 25, Spring Boot 4.1.x, gRPC-сервер (Spring grpc-server)
- Spring Data MongoDB (реактивный стек — Reactor, `ReactiveMongoTemplate`)
- MongoDB (база `wise_task_graph`, коллекция `graph`)

## Модель данных

```
Graph: id (UUID-строка), authorId, vertexCount, edgeCount, isDirect,
       isNamed, name, vertexList[], edgeList[]
Vertex: id, weight, label, xCoordinate, yCoordinate, color
Edge:   source, target, weight, label, color
Color:  GRAY | RED | BLUE | GREEN
```

Важно: `id` и `authorId` хранятся строками в каноническом формате UUID. Исторически часть документов была записана с legacy-типом Binary — если после восстановления из старого бэкапа `getAllGraphs` падает с `ConverterNotFoundException`, документы нужно привести к строковым `id`.

## gRPC API (порт 6565)

Proto: `src/main/resources/proto/graph_grpc.proto`, пакет `ru.leti.wise.task.graph`.

| Метод | Назначение |
|---|---|
| `getGraphById` | Получить граф по id (NOT_FOUND, если нет) |
| `createGraph` | Сохранить граф (id генерируется на сервере) |
| `generateRandomGraph` | Сгенерировать случайный граф по параметрам (вершины/рёбра/ориентированность) |
| `getAllGraphs` | Список с пагинацией и фильтрами |
| `GetGraphsByIds` | Получить графы пакетом по списку id |
| `removeGraph` | Удалить граф |

Фильтры `getAllGraphs`: `id`, `authorId`, `vertexCount`, `edgeCount`, `isDirect`, `isNamed`, `name` (regex, без учёта регистра). Если пагинация не передана, используется `page=0, pageSize=20`.

## Конфигурация

| Переменная | Назначение |
|---|---|
| `GRAPH_GRPC_PORT` | Порт gRPC-сервера (6565) |
| `MONGO_PORT` / `spring.data.mongodb.*` | Подключение к MongoDB |

## Запуск

Через общий docker-стенд gateway (см. `wise-task-gateway/docker-compose.yaml`):

```bash
cd ../wise-task-gateway && docker compose up -d --build wise-task-graph
```

Локально: `./gradlew bootRun` (нужна MongoDB, по умолчанию `localhost:27017`).

Быстрая проверка:

```bash
grpcurl -plaintext -import-path src/main/resources/proto -proto graph_grpc.proto \
  -d '{"pagination": {"page": 0, "pageSize": 10}}' \
  localhost:6565 ru.leti.wise.task.graph.GraphService/getAllGraphs
```
