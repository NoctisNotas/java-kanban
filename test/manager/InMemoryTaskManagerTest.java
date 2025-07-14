package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import util.TaskStatus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;
import java.time.LocalDateTime;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void shouldNotAllowTaskToOverlapWithExistingTasks() {
        Task task1 = new Task("Task 1", TaskStatus.NEW, "Desc",
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(30));
        getTaskManager().createTask(task1);

        Task overlappingTask = new Task("Task 2", TaskStatus.NEW, "Desc",
                LocalDateTime.of(2025, 1, 1, 10, 15), Duration.ofMinutes(30));

        assertThrows(IllegalStateException.class, () -> getTaskManager().createTask(overlappingTask));
    }

    @Test
    void shouldAllowNonOverlappingTasks() {
        Task task1 = new Task("Task 1", TaskStatus.NEW, "Desc",
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(30));
        getTaskManager().createTask(task1);

        Task nonOverlappingTask = new Task("Task 2", TaskStatus.NEW, "Desc",
                LocalDateTime.of(2025, 1, 1, 11, 0), Duration.ofMinutes(30));

        assertDoesNotThrow(() -> getTaskManager().createTask(nonOverlappingTask));
    }

    @Test
    void shouldCalculateEpicTimeFieldsCorrectly() {
        Epic epic = new Epic("Test Epic", "Description");

        Subtask subtask = new Subtask("Sub", TaskStatus.NEW, "Desc", epic.getId(),
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(30));

        epic.addSubtaskId(subtask.getId());
        assertEquals(LocalDateTime.of(2023, 1, 1, 10, 0), subtask.getStartTime());
    }

    @Test
    void shouldHandleEpicWithNoSubtasksTimeFields() {
        Epic epic = new Epic("Epic", "Description");
        getTaskManager().createEpic(epic);

        assertNull(epic.getStartTime());
        assertEquals(Duration.ZERO, epic.getDuration());
        assertNull(epic.getEndTime());
    }

    @Test
    void shouldHandleSubtaskWithoutEpic() {
        Subtask subtask = new Subtask("Subtask", TaskStatus.NEW, "Desc", 999);
        assertThrows(IllegalArgumentException.class, () -> getTaskManager().createSubtask(subtask));
    }
}