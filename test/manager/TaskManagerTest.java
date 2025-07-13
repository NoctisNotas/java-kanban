package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    void shouldCreateAndGetTask() {
        Task task = new Task("Test task", TaskStatus.NEW, "Description");
        taskManager.createTask(task);

        Task savedTask = taskManager.getTask(task.getId());
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");
    }

    @Test
    void shouldCreateAndGetEpic() {
        Epic epic = new Epic("Test epic", "Description");
        taskManager.createEpic(epic);

        Epic savedEpic = taskManager.getEpic(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic, "Эпики не совпадают");
    }

    @Test
    void shouldCreateAndGetSubtask() {
        Epic epic = new Epic("Test epic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Test subtask", TaskStatus.NEW, "Description", epic.getId());
        taskManager.createSubtask(subtask);

        Subtask savedSubtask = taskManager.getSubtask(subtask.getId());
        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают");
    }

    @Test
    void shouldUpdateTaskStatus() {
        Task task = new Task("Test task", TaskStatus.NEW, "Description");
        taskManager.createTask(task);

        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTask(task.getId()).getStatus(),
                "Статус задачи не обновился");
    }

    @Test
    void shouldReturnEmptyListWhenNoTasks() {
        assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач должен быть пустым");
        assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков должен быть пустым");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Список подзадач должен быть пустым");
    }

    @Test
    void shouldDeleteAllTasks() {
        Task task = new Task("Test task", TaskStatus.NEW, "Description");
        taskManager.createTask(task);

        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Задачи должны быть удалены");
    }

    @Test
    void shouldDeleteTaskById() {
        Task task = new Task("Test task", TaskStatus.NEW, "Description");
        taskManager.createTask(task);

        taskManager.deleteTask(task.getId());
        assertNull(taskManager.getTask(task.getId()), "Задача должна быть удалена");
    }

    @Test
    void shouldCalculateTaskEndTime() {
        Task task = new Task("Test task", TaskStatus.NEW, "Description",
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(30));
        assertEquals(LocalDateTime.of(2023, 1, 1, 10, 30), task.getEndTime());
    }

    @Test
    void shouldReturnPrioritizedTasks() {
        Task task1 = new Task("Task 1", TaskStatus.NEW, "Desc",
                LocalDateTime.of(2023, 1, 1, 11, 0), Duration.ofMinutes(30));
        Task task2 = new Task("Task 2", TaskStatus.NEW, "Desc",
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(30));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(task2.getId(), prioritized.get(0).getId());
        assertEquals(task1.getId(), prioritized.get(1).getId());
    }

    @Test
    void shouldDetectTimeOverlap() {
        Task task1 = new Task("Task 1", TaskStatus.NEW, "Desc",
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(30));
        taskManager.createTask(task1);

        Task task2 = new Task("Task 2", TaskStatus.NEW, "Desc",
                LocalDateTime.of(2023, 1, 1, 10, 15), Duration.ofMinutes(30));

        assertTrue(taskManager.hasTimeOverlap(task2), "Должно быть пересечение по времени");
    }

    @Test
    void epicStatusShouldBeNewWhenNoSubtasks() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика без подзадач должен быть NEW");
    }

    @Test
    void epicStatusShouldBeNewWhenAllSubtasksNew() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", TaskStatus.NEW, "Desc", epic.getId());
        taskManager.createSubtask(subtask);

        assertEquals(TaskStatus.NEW, epic.getStatus(),
                "Статус эпика со всеми подзадачами NEW должен быть NEW");
    }

    @Test
    void epicStatusShouldBeDoneWhenAllSubtasksDone() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", TaskStatus.DONE, "Desc", epic.getId());
        taskManager.createSubtask(subtask);

        assertEquals(TaskStatus.DONE, epic.getStatus(),
                "Статус эпика со всеми подзадачами DONE должен быть DONE");
    }

    @Test
    void epicStatusShouldBeInProgressWhenSubtasksMixed() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", TaskStatus.NEW, "Desc", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", TaskStatus.DONE, "Desc", epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Статус эпика с подзадачами NEW и DONE должен быть IN_PROGRESS");
    }

    @Test
    void epicStatusShouldBeInProgressWhenAnySubtaskInProgress() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", TaskStatus.IN_PROGRESS, "Desc", epic.getId());
        taskManager.createSubtask(subtask);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Статус эпика с любой подзадачей IN_PROGRESS должен быть IN_PROGRESS");
    }
}