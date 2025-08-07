package http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import manager.TaskManager;
import model.Subtask;
import util.GsonConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = GsonConfig.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (method.equals("GET")) {
                if (path.equals("/subtasks")) {
                    handleGetAllSubtasks(exchange);
                    return;
                } else if (path.matches("/subtasks/\\d+")) {
                    handleGetSubtask(exchange);
                    return;
                }
            } else if (method.equals("POST") && path.equals("/subtasks")) {
                handlePostSubtask(exchange);
                return;
            } else if (method.equals("DELETE") && path.matches("/subtasks/\\d+")) {
                handleDeleteSubtask(exchange);
                return;
            }

            sendNotFound(exchange);
        } catch (NotFoundException e) {
            handleNotFoundException(exchange, e);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        sendText(exchange, gson.toJson(subtasks));
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException, NotFoundException {
        int id = extractId(exchange.getRequestURI().getPath());
        Subtask subtask = taskManager.getSubtask(id);
        sendText(exchange, gson.toJson(subtask));
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        try {
            String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
            if (!"application/json".equalsIgnoreCase(contentType)) {
                sendText(exchange, "{\"error\":\"Content-Type must be application/json\"}", 415);
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = gson.fromJson(body, Subtask.class);

            if (subtask.getName() == null || subtask.getStatus() == null || subtask.getEpicId() == 0) {
                sendText(exchange, "{\"error\":\"Name, status and epicId are required\"}", 400);
                return;
            }

            Subtask createdSubtask;
            if (subtask.getId() == 0) {
                createdSubtask = taskManager.createSubtask(subtask);
                sendText(exchange, gson.toJson(createdSubtask), 201);
            } else {
                taskManager.updateSubtask(subtask);
                sendText(exchange, gson.toJson(subtask), 200);
            }
        } catch (JsonSyntaxException e) {
            sendText(exchange, "{\"error\":\"Invalid JSON format\"}", 400);
        } catch (IllegalStateException e) {
            sendText(exchange, "{\"error\":\"Time overlap with existing tasks\"}", 406);
        } catch (Exception e) {
            sendText(exchange, "{\"error\":\"Internal server error\"}", 500);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException, NotFoundException {
        int id = extractId(exchange.getRequestURI().getPath());
        taskManager.deleteSubtask(id);
        sendText(exchange, "{\"message\":\"Subtask deleted\"}");
    }

    private int extractId(String path) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[2]);
    }
}
