package manager;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> browsingHistory = new ArrayList<>();
    private static final int MAX_SIZE = 10;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        if (browsingHistory.size() == MAX_SIZE) {
            browsingHistory.removeFirst();
        }

        // Создаем копию задачи перед добавлением в историю
        Task taskCopy = copyTask(task);
        browsingHistory.add(taskCopy);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(browsingHistory);
    }

    private Task copyTask(Task original) {
        if (original == null) {
            return null;
        }

        Task copy = new Task(original.getName(), original.getStatus(), original.getDescription());
        copy.setId(original.getId());
        return copy;
    }
}
