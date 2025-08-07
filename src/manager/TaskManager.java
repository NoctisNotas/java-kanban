package manager;

import exceptions.NotFoundException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();

    void deleteAllTasks();

    void deleteTask(int id) throws NotFoundException;

    Task getTask(int id) throws NotFoundException;

    Task createTask(Task task);

    void updateTask(Task task);

    List<Epic> getAllEpics();

    void deleteAllEpics();

    void deleteEpic(int id) throws NotFoundException;

    Epic getEpic(int id) throws NotFoundException;

    Epic createEpic(Epic epic);

    void updateEpic(Epic epic);

    List<Subtask> getEpicSubtasks(int epicId);

    List<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Subtask getSubtask(int id) throws NotFoundException;

    Subtask createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtask(int id) throws NotFoundException;

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    boolean hasTimeOverlap(Task task);
}