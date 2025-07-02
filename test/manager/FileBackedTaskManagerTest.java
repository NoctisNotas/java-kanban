package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import model.Epic;
import model.Subtask;
import model.Task;
import util.TaskStatus;

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void shouldSaveAndLoadEmptyManager() {
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loaded.getAllTasks().isEmpty());
        assertTrue(loaded.getAllEpics().isEmpty());
        assertTrue(loaded.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldSaveAndLoadTasks() {
        Task task1 = new Task("Task 1", TaskStatus.NEW, "Description 1");
        Task task2 = new Task("Task 2", TaskStatus.IN_PROGRESS, "Description 2");
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic = new Epic("Epic 1", "Epic Description");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", TaskStatus.DONE, "Subtask Description", epic.getId());
        manager.createSubtask(subtask);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> tasks = loadedManager.getAllTasks();
        assertEquals(2, tasks.size());
        assertEquals("Task 1", tasks.getFirst().getName());
        assertEquals(TaskStatus.NEW, tasks.getFirst().getStatus());

        List<Epic> epics = loadedManager.getAllEpics();
        assertEquals(1, epics.size());
        assertEquals("Epic 1", epics.getFirst().getName());

        List<Subtask> subtasks = loadedManager.getAllSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals("Subtask 1", subtasks.getFirst().getName());
        assertEquals(epic.getId(), subtasks.getFirst().getEpicId());
    }

    @Test
    void shouldSaveAndLoadMultipleTasks() {
        for (int i = 1; i <= 5; i++) {
            Task task = new Task("Task " + i, TaskStatus.NEW, "Description " + i);
            manager.createTask(task);
        }
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> tasks = loadedManager.getAllTasks();
        assertEquals(5, tasks.size());
        for (int i = 0; i < 5; i++) {
            assertEquals("Task " + (i + 1), tasks.get(i).getName());
            assertEquals("Description " + (i + 1), tasks.get(i).getDescription());
        }
    }

    @Test
    void shouldSaveAndLoadEpicWithSubtasks() {
        Epic epic = new Epic("Epic", "Epic with subtasks");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", TaskStatus.NEW, "Desc 1", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", TaskStatus.DONE, "Desc 2", epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Epic> epics = loadedManager.getAllEpics();
        assertEquals(1, epics.size());
        assertEquals("Epic", epics.getFirst().getName());

        List<Subtask> subtasks = loadedManager.getAllSubtasks();
        assertEquals(2, subtasks.size());
        assertEquals(epic.getId(), subtasks.get(0).getEpicId());
        assertEquals(epic.getId(), subtasks.get(1).getEpicId());
    }

    @Test
    void shouldPreserveTaskIdsAfterLoading() {
        Task task = new Task("Task", TaskStatus.NEW, "Description");
        task.setId(10);
        manager.createTask(task);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(10, loadedManager.getAllTasks().getFirst().getId());
    }
}
