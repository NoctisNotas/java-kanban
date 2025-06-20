package manager;

import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        remove(task.getId());
        linkLast(copyTask(task));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
        history.remove(id);
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node currentNode = first;

        while (currentNode != null) {
            tasks.add(currentNode.value);
            currentNode = currentNode.next;
        }

        return tasks;
    }

    private void linkLast(Task task) {
        final Node oldLast = last;
        final Node newLast = new Node(last, task, null);
        last = newLast;
        history.put(task.getId(), newLast);

        if (oldLast == null) {
            first = newLast;
        } else {
            oldLast.next = newLast;
        }
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        node.value = null;

        if (node.prev == null && node.next == null) {
            first = null;
            last = null;
        } else if (node.prev == null) {
            first = node.next;
            first.prev = null;
        } else if (node.next == null) {
            last = node.prev;
            last.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

    }

    private Task copyTask(Task original) {
        if (original == null) {
            return null;
        }

        Task copy = new Task(original.getName(), original.getStatus(), original.getDescription());
        copy.setId(original.getId());
        return copy;
    }

    private static class Node {
        public Node prev;
        public Task value;
        public Node next;


        public Node(Node prev, Task value, Node next) {
            this.prev = prev;
            this.value = value;
            this.next = next;
        }
    }
}


