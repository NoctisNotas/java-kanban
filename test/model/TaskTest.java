package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import util.TaskStatus;

class TaskTest {
    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Task 1", TaskStatus.NEW, "Description 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", TaskStatus.DONE, "Description 2");
        task2.setId(1);

        assertEquals(task1, task2, "Tasks with the same ID should be equal");
        assertEquals(task1.hashCode(), task2.hashCode(), "Tasks hashCode with the same ID should be equal");
    }

    @Test
    public void tasksWithDifferentIdShouldNotBeEqual() {
        Task task1 = new Task("Task 1", TaskStatus.NEW, "Description 1");
        task1.setId(1);
        Task task2 = new Task("Task 1", TaskStatus.NEW, "Description 1");
        task2.setId(2);

        assertNotEquals(task1, task2, "Tasks with the different ID should not be equal");
    }
}