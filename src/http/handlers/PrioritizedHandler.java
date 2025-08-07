package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;
import util.GsonConfig;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = GsonConfig.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!exchange.getRequestMethod().equals("GET")) {
                sendNotFound(exchange);
                return;
            }

            if (!exchange.getRequestURI().getPath().equals("/prioritized")) {
                sendNotFound(exchange);
                return;
            }

            List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            sendText(exchange, gson.toJson(prioritizedTasks));
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}
