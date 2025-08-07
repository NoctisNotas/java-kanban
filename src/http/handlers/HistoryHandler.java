package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;
import util.GsonConfig;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager) {
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

            if (!exchange.getRequestURI().getPath().equals("/history")) {
                sendNotFound(exchange);
                return;
            }

            List<Task> history = taskManager.getHistory();
            sendText(exchange, gson.toJson(history));
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}
