package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import util.Managers;
import util.TaskStatus;
import model.Task;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Test task", TaskStatus.NEW, "Description");
        task.setId(1);
    }

    @Test
    void shouldAddTaskToHistory() {
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.getFirst());
    }

    @Test
    void shouldNotAddDuplicateTasks() {
        historyManager.add(task);
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void shouldMoveTaskToEndWhenRepeated() {
        Task task2 = new Task("Task 2", TaskStatus.IN_PROGRESS, "Desc");
        task2.setId(2);

        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task, history.get(1));
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        historyManager.add(task);
        historyManager.remove(task.getId());
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldNotFailWhenRemovingNonExistentTask() {
        historyManager.remove(37);
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldKeepHistoryOrder() {
        Task task2 = new Task("Task 2", TaskStatus.DONE, "Desc");
        task2.setId(2);

        historyManager.add(task);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(task, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void shouldUpdateTaskInHistory() {
        historyManager.add(task);
        task.setStatus(TaskStatus.DONE);
        historyManager.add(task);

        Task fromHistory = historyManager.getHistory().getFirst();
        assertEquals(TaskStatus.DONE, fromHistory.getStatus());
    }
}