package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import util.TaskStatus;

class SubtaskTest {
    @Test
    void subtasksWithSameIdShouldBeEqual() {
        Subtask subtask1 = new Subtask("Subtask 1", TaskStatus.NEW,"Description 1", 5);
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("Subtask 2", TaskStatus.DONE,"Description 2", 5);
        subtask2.setId(1);

        assertEquals(subtask1, subtask2, "Subtasks with the same ID should be equal");
        assertEquals(subtask1.hashCode(), subtask2.hashCode(), "Subtasks hashCode with the same ID should be equal");
    }

    @Test
    public void subtasksWithDifferentIdShouldNotBeEqual() {
        Subtask subtask1 = new Subtask("Subtask 1", TaskStatus.NEW,"Description 1", 5);
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("Subtask 1", TaskStatus.NEW,"Description 1", 5);
        subtask1.setId(2);

        assertNotEquals(subtask1, subtask2, "Subtasks with the different ID should not be equal");
    }

}