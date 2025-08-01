package manager;

import exceptions.NotFoundException;
import model.Epic;
import model.Subtask;
import model.Task;
import util.Managers;
import util.TaskStatus;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;
    private final HistoryManager browsingHistory = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder()))
    );

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public boolean hasTimeOverlap(Task task) {
        if (task.getStartTime() == null || task.getEndTime() == null) {
            return false;
        }

        return prioritizedTasks.stream()
                .filter(t -> t.getStartTime() != null && t.getEndTime() != null)
                .anyMatch(existingTask -> isTimeOverlapping(task, existingTask));
    }

    private boolean isTimeOverlapping(Task task1, Task task2) {
        return task1.getStartTime().isBefore(task2.getEndTime()) &&
                task1.getEndTime().isAfter(task2.getStartTime());
    }

    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritizedTasks(Task task) {
        prioritizedTasks.remove(task);
    }

    @Override
    public List<Task> getHistory() {
        return browsingHistory.getHistory();
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.values().forEach(this::removeFromPrioritizedTasks);
        tasks.clear();
    }

    @Override
    public void deleteTask(int id) throws NotFoundException {
        Task task = tasks.remove(id);
        if (task == null) {
            throw new NotFoundException("Task with id=" + id + " not found");
        }
        browsingHistory.remove(id);
        removeFromPrioritizedTasks(task);
    }

    @Override
    public Task getTask(int id) throws NotFoundException {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Task with id=" + id + " not found");
        }
        browsingHistory.add(task);
        return task;
    }

    @Override
    public Task createTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        if (task.getId() != 0) {
            if (tasks.containsKey(task.getId())) {
                throw new IllegalArgumentException("Task ID " + task.getId() + " already exists");
            }
            if (task.getId() >= nextId) {
                nextId = task.getId() + 1;
            }
        } else {
            task.setId(nextId++);
        }

        if (hasTimeOverlap(task)) {
            throw new IllegalStateException("Task time overlaps with existing task");
        }

        tasks.put(task.getId(), task);
        addToPrioritizedTasks(task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task oldTask = tasks.get(task.getId());
            removeFromPrioritizedTasks(oldTask);

            if (hasTimeOverlap(task)) {
                addToPrioritizedTasks(oldTask); // Restore old task if new time is invalid
                throw new IllegalStateException("Updated task time overlaps with existing task");
            }

            tasks.put(task.getId(), task);
            addToPrioritizedTasks(task);
        }
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.values().forEach(epic -> {
            browsingHistory.remove(epic.getId());
            epic.getSubtaskId().forEach(subtaskId -> {
                browsingHistory.remove(subtaskId);
                removeFromPrioritizedTasks(subtasks.get(subtaskId));
            });
        });
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteEpic(int id) throws NotFoundException {
        Epic epic = epics.remove(id);
        if (epic == null) {
            throw new NotFoundException("Epic with id=" + id + " not found");
        }
        browsingHistory.remove(id);
        epic.getSubtaskId().forEach(subtaskId -> {
            browsingHistory.remove(subtaskId);
            removeFromPrioritizedTasks(subtasks.remove(subtaskId));
        });
    }

    @Override
    public Epic getEpic(int id) throws NotFoundException {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Epic with id=" + id + " not found");
        }
        browsingHistory.add(epic);
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Epic cannot be null");
        }

        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic updatedEpic = epics.get(epic.getId());
            updatedEpic.setName(epic.getName());
            updatedEpic.setDescription(epic.getDescription());
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return Collections.emptyList();
        }
        return epic.getSubtaskId().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.values().forEach(this::removeFromPrioritizedTasks);
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtaskId().clear();
            updateEpicStatus(epic.getId());
        });
    }

    @Override
    public Subtask getSubtask(int id) throws NotFoundException {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Subtask with id=" + id + " not found");
        }
        browsingHistory.add(subtask);
        return subtask;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask cannot be null");
        }

        if (subtask.getId() == subtask.getEpicId()) {
            throw new IllegalArgumentException("Subtask cannot be its own epic");
        }

        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Epic with id " + subtask.getEpicId() + " not found");
        }

        if (hasTimeOverlap(subtask)) {
            throw new IllegalStateException("Subtask time overlaps with existing task");
        }

        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        addToPrioritizedTasks(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic.getId());

        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask oldSubtask = subtasks.get(subtask.getId());
            removeFromPrioritizedTasks(oldSubtask);

            if (hasTimeOverlap(subtask)) {
                addToPrioritizedTasks(oldSubtask); // Restore old subtask if new time is invalid
                throw new IllegalStateException("Updated subtask time overlaps with existing task");
            }

            subtasks.put(subtask.getId(), subtask);
            addToPrioritizedTasks(subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    @Override
    public void deleteSubtask(int id) throws NotFoundException {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            throw new NotFoundException("Subtask with id=" + id + " not found");
        }
        browsingHistory.remove(id);
        removeFromPrioritizedTasks(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.removeSubtaskId(id);
            updateEpicStatus(epic.getId());
        }
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        if (epic.getSubtaskId().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (int subtaskId : epic.getSubtaskId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) continue;

            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}