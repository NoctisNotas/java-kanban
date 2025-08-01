package http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import util.GsonConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = GsonConfig.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (method.equals("GET")) {
                if (path.equals("/epics")) {
                    handleGetAllEpics(exchange);
                    return;
                } else if (path.matches("/epics/\\d+")) {
                    handleGetEpic(exchange);
                    return;
                } else if (path.matches("/epics/\\d+/subtasks")) {
                    handleGetEpicSubtasks(exchange);
                    return;
                }
            } else if (method.equals("POST") && path.equals("/epics")) {
                handlePostEpic(exchange);
                return;
            } else if (method.equals("DELETE") && path.matches("/epics/\\d+")) {
                handleDeleteEpic(exchange);
                return;
            }

            sendNotFound(exchange);
        } catch (NotFoundException e) {
            handleNotFoundException(exchange, e);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();
        sendText(exchange, gson.toJson(epics));
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException, NotFoundException {
        int id = extractId(exchange.getRequestURI().getPath());
        Epic epic = taskManager.getEpic(id);
        sendText(exchange, gson.toJson(epic));
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException, NotFoundException {
        int epicId = extractId(exchange.getRequestURI().getPath());
        List<Subtask> subtasks = taskManager.getEpicSubtasks(epicId);
        sendText(exchange, gson.toJson(subtasks));
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        try {
            String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
            if (!"application/json".equalsIgnoreCase(contentType)) {
                sendText(exchange, "{\"error\":\"Content-Type must be application/json\"}", 415);
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(body, Epic.class);

            if (epic.getName() == null) {
                sendText(exchange, "{\"error\":\"Name is required\"}", 400);
                return;
            }

            Epic createdEpic;
            if (epic.getId() == 0) {
                createdEpic = taskManager.createEpic(epic);
                sendText(exchange, gson.toJson(createdEpic), 201);
            } else {
                taskManager.updateEpic(epic);
                sendText(exchange, gson.toJson(epic), 200);
            }
        } catch (JsonSyntaxException e) {
            sendText(exchange, "{\"error\":\"Invalid JSON format\"}", 400);
        } catch (Exception e) {
            sendText(exchange, "{\"error\":\"Internal server error\"}", 500);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException, NotFoundException {
        int id = extractId(exchange.getRequestURI().getPath());
        taskManager.deleteEpic(id);
        sendText(exchange, "{\"message\":\"Epic deleted\"}");
    }

    private int extractId(String path) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[2]);
    }
}