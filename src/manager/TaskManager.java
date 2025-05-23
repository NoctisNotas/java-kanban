package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    //методы для класса Task
    ArrayList<Task> getAllTasks();

    void deleteAllTasks();

    void deleteTask(int id);

    Task getTask(int id);

    Task createTask(Task task);

    void updateTask(Task task);

    //методы для класса Epic
    ArrayList<Epic> getAllEpics();

    void deleteAllEpics();

    void deleteEpic(int id);

    Epic getEpic(int id);

    Epic createEpic(Epic epic);

    void updateEpic(Epic epic);

    ArrayList<Subtask> getEpicSubtasks(int epicId);

    //методы для класса Subtask
    ArrayList<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Subtask getSubtask(int id);

    Subtask createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtask(int id);

    List<Task> getHistory();
}
