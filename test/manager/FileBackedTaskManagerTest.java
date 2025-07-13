package manager;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            tempFile = File.createTempFile("tasks", ".csv");
            return new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp file", e);
        }
    }

    @BeforeEach
    void clearFile() throws IOException {
        if (tempFile != null && tempFile.exists()) {
            Files.write(tempFile.toPath(), new byte[0]);
        }
    }

    @Test
    void shouldSaveAndLoadEmptyManager() {
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loaded.getAllTasks().isEmpty());
        assertTrue(loaded.getAllEpics().isEmpty());
        assertTrue(loaded.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldSaveAndLoadTasksWithTimeFields() {
        Task task = new Task("Task", TaskStatus.NEW, "Desc",
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(30));
        taskManager.createTask(task);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loaded.getTask(task.getId());

        assertNotNull(loadedTask);
        assertEquals(task.getStartTime(), loadedTask.getStartTime());
        assertEquals(task.getDuration(), loadedTask.getDuration());
    }

    @Test
    void shouldSaveAndLoadEpicWithSubtasks() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Sub", TaskStatus.NEW, "Desc", epic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createSubtask(subtask);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(1, loaded.getEpicSubtasks(epic.getId()).size());
    }

    @Test
    void shouldHandleFileReadError() {
        File invalidFile = new File("nonexistent_directory/tasks.csv");
        assertThrows(ManagerSaveException.class, () ->
                FileBackedTaskManager.loadFromFile(invalidFile));
    }

    @Test
    void shouldHandleFileWriteError() {
        File readOnlyFile = new File("readonly.csv");
        readOnlyFile.setReadOnly();

        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager manager = new FileBackedTaskManager(readOnlyFile);
            manager.createTask(new Task("Test", TaskStatus.NEW, "Desc"));
        });
    }
}