package manager;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task 1", TaskStatus.NEW, "Description");
        task1.setId(1);
        task2 = new Task("Task 2", TaskStatus.IN_PROGRESS, "Description");
        task2.setId(2);
        task3 = new Task("Task 3", TaskStatus.DONE, "Description",
                LocalDateTime.now(), Duration.ofMinutes(30));
        task3.setId(3);
    }

    @Test
    void shouldAddTasksToHistory() {
        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void shouldNotAddDuplicateTasks() {
        historyManager.add(task1);
        historyManager.add(task1);

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void shouldMoveTaskToEndWhenRepeated() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.getFirst());
    }

    @Test
    void shouldRemoveFromBeginning() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        assertEquals(List.of(task2), historyManager.getHistory());
    }

    @Test
    void shouldRemoveFromMiddle() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());

        assertEquals(List.of(task1, task3), historyManager.getHistory());
    }

    @Test
    void shouldRemoveFromEnd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId());

        assertEquals(List.of(task1, task2), historyManager.getHistory());
    }

    @Test
    void shouldHandleEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
        historyManager.remove(1);
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldPreserveTaskFieldsInHistory() {
        task1.setStatus(TaskStatus.DONE);
        historyManager.add(task1);

        Task fromHistory = historyManager.getHistory().getFirst();
        assertEquals(TaskStatus.DONE, fromHistory.getStatus());
        assertEquals("Task 1", fromHistory.getName());
    }

    @Test
    void shouldHandleTasksWithTimeFields() {
        historyManager.add(task3);
        Task fromHistory = historyManager.getHistory().getFirst();

        assertNotNull(fromHistory.getStartTime());
        assertNotNull(fromHistory.getDuration());
        assertEquals(task3.getEndTime(), fromHistory.getEndTime());
    }
}