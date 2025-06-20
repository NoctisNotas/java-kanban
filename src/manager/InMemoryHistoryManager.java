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
        Node node = history.get(id);
        if (node != null) {
            removeNode(node);
            history.remove(id);
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node currentNode = first;

        while (currentNode != null) {
            tasks.add(currentNode.getValue());
            currentNode = currentNode.getNext();
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
            oldLast.setNext(newLast);
        }
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        Node prev = node.getPrev();
        Node next = node.getNext();

        if (prev == null && next == null) {
            first = null;
            last = null;
        } else if (prev == null) {
            first = next;
            first.setPrev(null);
        } else if (next == null) {
            last = prev;
            last.setNext(null);
        } else {
            prev.setNext(next);
            next.setPrev(prev);
        }

        node.clearNode();
    }

    private Task copyTask(Task original) {
        if (original == null) {
            return null;
        }

        Task copy = new Task(original.getName(), original.getStatus(), original.getDescription());
        copy.setId(original.getId());
        return copy;
    }

    private static final class Node {
        private Node prev;
        private Task value;
        private Node next;

        public Node(Node prev, Task value, Node next) {
            this.prev = prev;
            this.value = value;
            this.next = next;
        }

        public Node getPrev() {
            return prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public Task getValue() {
            return value;
        }

        public void setValue(Task value) {
            this.value = value;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public void clearNode() {
            this.prev = null;
            this.value = null;
            this.next = null;
        }
    }
}


