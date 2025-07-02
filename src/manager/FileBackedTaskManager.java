package manager;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import util.TaskStatus;
import util.TaskType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (!line.isEmpty()) {
                    Task task = fromString(line);
                    if (task != null) {
                        switch (task.getType()) {
                            case TASK:
                                manager.createTask(task);
                                break;
                            case EPIC:
                                manager.createEpic((Epic) task);
                                break;
                            case SUBTASK:
                                manager.createSubtask((Subtask) task);
                                break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла");
        }
        return manager;
    }

    private void save() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                builder.append(toString(task)).append("\n");
            }

            for (Epic epic : getAllEpics()) {
                builder.append(toString(epic)).append("\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                builder.append(toString(subtask)).append("\n");
            }

            Files.writeString(file.toPath(), builder.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }

    private static String toString(Task task) {
        String type = task.getType().name();
        String name = task.getName();
        String status = task.getStatus().name();
        String description = task.getDescription();
        String epic = "";

        if (task.getType() == TaskType.SUBTASK) {
            epic = String.valueOf(((Subtask) task).getEpicId());
        }

        return String.format("%d,%s,%s,%s,%s,%s", task.getId(), type, name, status, description, epic);
    }

    private static Task fromString(String value) {
        String[] elements = value.split(",");
        int id = Integer.parseInt(elements[0]);
        TaskType type = TaskType.valueOf(elements[1]);
        String name = elements[2];
        TaskStatus status = TaskStatus.valueOf(elements[3]);
        String description = elements[4];

        switch (type) {
            case TASK:
                Task task = new Task(name, status, description);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            case SUBTASK:
                int epicId = elements[5].isEmpty() ? 0 : Integer.parseInt(elements[5]);
                Subtask subtask = new Subtask(name, status, description, epicId);
                subtask.setId(id);
                return subtask;
            default:
                return null;
        }
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        save();
        return createdSubtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }
}