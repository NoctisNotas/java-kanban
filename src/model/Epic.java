package model;

import java.util.Objects;
import java.util.ArrayList;

import util.TaskStatus;
import util.TaskType;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId;

    public Epic(String name, String description) {
        super(name, TaskStatus.NEW, description);
        subtasksId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtasksId;
    }

    public void addSubtaskId(int subtaskId) {
        subtasksId.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtasksId.remove((Integer) subtaskId);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        String result = "Epic{" + "subtasksId=" + subtasksId + ", name='" + super.getName() + '\'';

        if (super.getDescription() == null) {
            result += ", description=null";
        } else {
            result += ", description.length=" + super.getDescription().length();
        }

        return result += ", id=" + super.getId() + ", status=" + super.getStatus() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksId, epic.subtasksId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksId);
    }
}
