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

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void shouldPreservePreviousTaskVersionInHistory() {
        Task task = new Task("Task", TaskStatus.NEW, "Description");
        task.setId(1);

        historyManager.add(task);

        task.setName("Updated name");
        task.setStatus(TaskStatus.IN_PROGRESS);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals("Task", history.get(0).getName());
        assertEquals(TaskStatus.NEW, history.get(0).getStatus());
        assertEquals("Updated name", history.get(1).getName());
        assertEquals(TaskStatus.IN_PROGRESS, history.get(1).getStatus());
    }


}