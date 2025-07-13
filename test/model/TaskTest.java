package model;

import org.junit.jupiter.api.Test;
import util.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void shouldCorrectlyCalculateEndTime() {
        Task task = new Task("Task", TaskStatus.NEW, "Description",
                LocalDateTime.of(2025, 1, 1, 9, 0), Duration.ofHours(2));

        assertEquals(LocalDateTime.of(2025, 1, 1, 11, 0), task.getEndTime());
    }

    @Test
    void shouldReturnNullEndTimeWhenNoStartTime() {
        Task task = new Task("Task", TaskStatus.NEW, "Description");
        assertNull(task.getEndTime());
    }

    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Task1", TaskStatus.NEW, "Desc1");
        task1.setId(1);

        Task task2 = new Task("Task2", TaskStatus.DONE, "Desc2");
        task2.setId(1);

        assertEquals(task1, task2);
    }

    @Test
    public void tasksWithDifferentIdShouldNotBeEqual() {
        Task task1 = new Task("Task 1", TaskStatus.NEW, "Description 1");
        task1.setId(1);
        Task task2 = new Task("Task 1", TaskStatus.NEW, "Description 1");
        task2.setId(2);

        assertNotEquals(task1, task2, "Tasks with the different ID should not be equal");
    }

    @Test
    void shouldHandleTimeFieldChanges() {
        Task task = new Task("Task", TaskStatus.NEW, "Desc");
        assertNull(task.getStartTime());

        LocalDateTime time = LocalDateTime.now();
        task.setStartTime(time);
        assertEquals(time, task.getStartTime());

        Duration duration = Duration.ofMinutes(30);
        task.setDuration(duration);
        assertEquals(duration, task.getDuration());
    }
}