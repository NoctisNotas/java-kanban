public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Task1", TaskStatus.NEW, "Task Description1");
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", TaskStatus.IN_PROGRESS, "Task Description2");
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Epic1", "Epic Description1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1.1", TaskStatus.NEW, "SubTask Description1.1",
                epic1.getTaskIdentifier());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask1.2", TaskStatus.DONE, "SubTask Description1.2",
                epic1.getTaskIdentifier());
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Epic1", "Epic Description1");
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Subtask2.1", TaskStatus.DONE, "SubTask Description2.1",
                epic2.getTaskIdentifier());
        taskManager.createSubtask(subtask3);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println();

        subtask1.setTaskStatus(TaskStatus.DONE);
        subtask3.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpic(epic1);
        taskManager.updateEpic(epic2);

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println();

        taskManager.deleteTask(task2.getTaskIdentifier());
        taskManager.deleteSubtask(subtask1.getTaskIdentifier());
        taskManager.deleteEpic(epic2.getTaskIdentifier());

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
    }
}
