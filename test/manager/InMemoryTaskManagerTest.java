package manager;

import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Managers;
import util.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private static TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }


}