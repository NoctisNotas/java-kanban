import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import util.Managers;
import util.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // Создание задач с временными параметрами
        Task task1 = new Task("Task1", TaskStatus.NEW, "Description1",
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(30));
        taskManager.createTask(task1);

        Task task2 = new Task("Task2", TaskStatus.IN_PROGRESS, "Description2",
                LocalDateTime.of(2023, 1, 1, 11, 0), Duration.ofMinutes(45));
        taskManager.createTask(task2);

        // Эпик с подзадачами
        Epic epic1 = new Epic("Epic1", "Epic Description");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask1", TaskStatus.NEW, "SubDesc1",
                epic1.getId(), LocalDateTime.of(2023, 1, 2, 9, 0), Duration.ofHours(1));
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask2", TaskStatus.DONE, "SubDesc2",
                epic1.getId(), LocalDateTime.of(2023, 1, 2, 11, 0), Duration.ofMinutes(90));
        taskManager.createSubtask(subtask2);

        // Тестирование новых методов
        System.out.println("Prioritized tasks:");
        taskManager.getPrioritizedTasks().forEach(System.out::println);

        System.out.println("\nEpic time calculation:");
        System.out.println("Start: " + epic1.getStartTime());
        System.out.println("Duration: " + epic1.getDuration());
        System.out.println("End: " + epic1.getEndTime());

        // Проверка пересечений
        Task overlappingTask = new Task("Overlap", TaskStatus.NEW, "Desc",
                LocalDateTime.of(2023, 1, 1, 10, 15), Duration.ofMinutes(10));
        System.out.println("\nHas overlap: " + taskManager.hasTimeOverlap(overlappingTask));
    }
}