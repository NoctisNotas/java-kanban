package util;

import org.junit.jupiter.api.Test;

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
}