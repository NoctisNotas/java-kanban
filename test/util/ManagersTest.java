package util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void managerShouldBeInitialized() {
        assertNotNull(Managers.getDefault());
    }

    @Test
    void historyManagerShouldBeInitialized() {
        assertNotNull(Managers.getDefaultHistory());
    }

    @Test
    void fileBackedManagerShouldBeInitialized(@TempDir File tempDir) {
        File file = new File(tempDir, "tasks.csv");
        assertNotNull(Managers.getFileBackedManager(file));
    }
}