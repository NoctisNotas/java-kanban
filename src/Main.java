import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import util.Managers;
import util.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Task1", TaskStatus.NEW, "model.Task Description1");
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", TaskStatus.IN_PROGRESS, "model.Task Description2");
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Epic1", "model.Epic Description1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1.1", TaskStatus.NEW, "SubTask Description1.1",
                epic1.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask1.2", TaskStatus.DONE, "SubTask Description1.2",
                epic1.getId());
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Epic2", "model.Epic Description2");
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Subtask2.1", TaskStatus.DONE, "SubTask Description2.1",
                epic2.getId());
        taskManager.createSubtask(subtask3);

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getEpic(epic2.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getSubtask(subtask3.getId());
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getEpic(epic2.getId());

        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }

        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
