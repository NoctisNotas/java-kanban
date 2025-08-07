package http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import manager.TaskManager;
import model.Task;
import util.GsonConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = GsonConfig.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (method.equals("GET")) {
                if (path.equals("/tasks")) {
                    handleGetAllTasks(exchange);
                    return;
                } else if (path.matches("/tasks/\\d+")) {
                    handleGetTask(exchange);
                    return;
                }
            } else if (method.equals("POST") && path.equals("/tasks")) {
                handlePostTask(exchange);
                return;
            } else if (method.equals("DELETE") && path.matches("/tasks/\\d+")) {
                handleDeleteTask(exchange);
                return;
            }

            sendNotFound(exchange);
        } catch (NotFoundException e) {
            handleNotFoundException(exchange, e);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        sendText(exchange, gson.toJson(tasks));
    }

    private void handleGetTask(HttpExchange exchange) throws IOException, NotFoundException {
        int id = extractId(exchange.getRequestURI().getPath());
        Task task = taskManager.getTask(id);
        sendText(exchange, gson.toJson(task));
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        try {
            String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
            if (!"application/json".equalsIgnoreCase(contentType)) {
                sendText(exchange, "{\"error\":\"Content-Type must be application/json\"}", 415);
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(body, Task.class);

            if (task.getName() == null || task.getStatus() == null) {
                sendText(exchange, "{\"error\":\"Name and status are required\"}", 400);
                return;
            }

            Task createdTask;
            if (task.getId() == 0) {
                createdTask = taskManager.createTask(task);
                sendText(exchange, gson.toJson(createdTask), 201);
            } else {
                taskManager.updateTask(task);
                sendText(exchange, gson.toJson(task), 200);
            }
        } catch (JsonSyntaxException e) {
            sendText(exchange, "{\"error\":\"Invalid JSON format\"}", 400);
        } catch (IllegalStateException e) {
            sendText(exchange, "{\"error\":\"Time overlap with existing tasks\"}", 406);
        } catch (Exception e) {
            sendText(exchange, "{\"error\":\"Internal server error\"}", 500);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException, NotFoundException {
        int id = extractId(exchange.getRequestURI().getPath());
        taskManager.deleteTask(id);
        sendText(exchange, "{\"message\":\"Task deleted\"}");
    }

    private int extractId(String path) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[2]);
    }
}
