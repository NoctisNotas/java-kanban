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

        browsingHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(browsingHistory);
    }
}
