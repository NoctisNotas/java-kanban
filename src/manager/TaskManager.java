package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();

    void deleteAllTasks();

    void deleteTask(int id);

    Task getTask(int id);

    Task createTask(Task task);

    void updateTask(Task task);

    List<Epic> getAllEpics();

    void deleteAllEpics();

    void deleteEpic(int id);

    Epic getEpic(int id);

    Epic createEpic(Epic epic);

    void updateEpic(Epic epic);

    List<Subtask> getEpicSubtasks(int epicId);

    List<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Subtask getSubtask(int id);

    Subtask createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtask(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    boolean hasTimeOverlap(Task task);
}