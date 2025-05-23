package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import model.Epic;
import model.Subtask;
import model.Task;
import util.Managers;
import util.TaskStatus;

import java.util.List;

class InMemoryTaskManagerTest {

    private static TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldAddAndFindDifferentTaskTypes() {
        Task task = new Task("Task", TaskStatus.NEW, "Description");
        task = taskManager.createTask(task);

        Epic epic = new Epic("Epic", "Description");
        epic = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", TaskStatus.NEW, "Description", epic.getId());
        subtask = taskManager.createSubtask(subtask);

        assertEquals(task, taskManager.getTask(task.getId()));
        assertEquals(epic, taskManager.getEpic(epic.getId()));
        assertEquals(subtask, taskManager.getSubtask(subtask.getId()));
    }

    @Test
    void shouldHandleManualAndAutoGeneratedIds() {
        Task task1 = new Task("Task1", TaskStatus.NEW, "Description");
        task1.setId(100); // Явно задаём ID
        task1 = taskManager.createTask(task1);

        Task task2 = new Task("Task2", TaskStatus.NEW, "Description");
        task2 = taskManager.createTask(task2); // Должен получить nextId (101)

        assertEquals(100, task1.getId());
        assertTrue(task2.getId() > 100);
        assertNotEquals(task1.getId(), task2.getId());

        Task task3 = new Task("Task3", TaskStatus.NEW, "Description");
        task3.setId(100);

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createTask(task3);
        });
    }

    @Test
    void shouldPreserveTaskFieldsWhenAddedToManager() {
        Task task = new Task("Task", TaskStatus.NEW, "Description");
        task.setId(1);

        Task addedTask = taskManager.createTask(task);

        assertEquals(task.getName(), addedTask.getName());
        assertEquals(task.getDescription(), addedTask.getDescription());
        assertEquals(task.getStatus(), addedTask.getStatus());
    }

    @Test
    void epicStatusShouldUpdateBasedOnSubtasks() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Sub1", TaskStatus.NEW, "Desc", epic.getId());
        Subtask subtask2 = new Subtask("Sub2", TaskStatus.DONE, "Desc", epic.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void updateNonExistentTaskShouldNotAffectManager() {
        Task task = new Task("Task", TaskStatus.NEW, "Description");
        task.setId(555);

        taskManager.updateTask(task);
        assertNull(taskManager.getTask(55));
    }

    @Test
    void deleteAllTasksShouldClearTasksAndKeepEpics() {
        Task task = new Task("Test", TaskStatus.NEW, "Description");
        taskManager.createTask(task);

        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);
        taskManager.deleteAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty());
        assertFalse(taskManager.getAllEpics().isEmpty());
    }
}