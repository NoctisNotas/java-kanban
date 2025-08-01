package http;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Subtask;
import model.Epic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TaskStatus;
import util.GsonConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class SubtasksHandlerTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;
    private Epic testEpic;
    private final String baseUrl = "http://localhost:8080/subtasks";

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
        gson = GsonConfig.getGson();
        client = HttpClient.newHttpClient();
        testEpic = manager.createEpic(new Epic("Test Epic", "Description"));
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void testCreateSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Test Subtask", TaskStatus.NEW, "Description", testEpic.getId());
        String subtaskJson = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllSubtasks().size());
    }

    @Test
    void testGetSubtaskById() throws IOException, InterruptedException {
        Subtask subtask = manager.createSubtask(new Subtask("Test", TaskStatus.NEW, "Desc", testEpic.getId()));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + subtask.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void testUpdateSubtask() throws IOException, InterruptedException {
        Subtask subtask = manager.createSubtask(new Subtask("Original", TaskStatus.NEW, "Desc", testEpic.getId()));
        subtask.setName("Updated");
        String subtaskJson = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Updated", manager.getSubtask(subtask.getId()).getName());
    }

    @Test
    void testDeleteSubtask() throws IOException, InterruptedException {
        Subtask subtask = manager.createSubtask(new Subtask("To delete", TaskStatus.NEW, "Desc", testEpic.getId()));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + subtask.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    void testGetEpicSubtasks() throws IOException, InterruptedException {
        manager.createSubtask(new Subtask("Subtask 1", TaskStatus.NEW, "Desc", testEpic.getId()));
        manager.createSubtask(new Subtask("Subtask 2", TaskStatus.DONE, "Desc", testEpic.getId()));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "?epicId=" + testEpic.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Subtask 1"));
        assertTrue(response.body().contains("Subtask 2"));
    }
}