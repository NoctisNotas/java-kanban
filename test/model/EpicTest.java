package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void epicsWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        epic1.setId(1);
        epic1.addSubtaskId(55);
        Epic epic2 = new Epic("Epic 2", "Description 2");
        epic2.setId(1);
        epic2.addSubtaskId(55);

        assertEquals(epic1, epic2, "Tasks with the same ID should be equal");
        assertEquals(epic1.hashCode(), epic2.hashCode(), "Tasks hashCode with the same ID should be equal");
    }

    @Test
    public void epicsWithDifferentIdShouldNotBeEqual() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        epic1.setId(1);
        epic1.addSubtaskId(55);
        Epic epic2 = new Epic("Epic 1", "Description 1");
        epic2.setId(2);
        epic2.addSubtaskId(54);

        assertNotEquals(epic1, epic2, "Tasks with the different ID should not be equal");
    }
}