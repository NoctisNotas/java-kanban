package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;
import util.TaskStatus;
import util.TaskType;

public class Epic extends Task {
    @SerializedName("subtasksId")
    private ArrayList<Integer> subtasksId;

    @SerializedName("endTime")
    private LocalDateTime endTime;

    public Epic() {
        this.subtasksId = new ArrayList<>();
    }

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
    public Duration getDuration() {
        if (subtasksId.isEmpty()) {
            return Duration.ZERO;
        }

        long totalMinutes = subtasksId.stream()
                .mapToLong(id -> {
                    Subtask subtask = (Subtask) getTaskById(id);
                    return subtask != null && subtask.getDuration() != null ?
                            subtask.getDuration().toMinutes() : 0;
                })
                .sum();

        return Duration.ofMinutes(totalMinutes);
    }

    @Override
    public LocalDateTime getStartTime() {
        if (subtasksId.isEmpty()) {
            return null;
        }

        return subtasksId.stream()
                .map(this::getTaskById)
                .filter(Objects::nonNull)
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public LocalDateTime getEndTime() {
        if (subtasksId.isEmpty()) {
            return null;
        }

        return subtasksId.stream()
                .map(this::getTaskById)
                .filter(Objects::nonNull)
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    private Task getTaskById(int id) {
        return null;
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

        return result += ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", endTime=" + getEndTime() + '}';
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
        return Objects.hash(super.hashCode(), subtasksId, endTime);
    }
}