package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;
import util.TaskStatus;
import util.TaskType;

public class Subtask extends Task {
    @SerializedName("epicId")
    private int epicId;

    public Subtask() {
    }

    public Subtask(String name, TaskStatus status, String description, int epicId) {
        super(name, status, description);
        this.epicId = epicId;
    }

    public Subtask(String name, TaskStatus status, String description,
                   int epicId, LocalDateTime startTime, Duration duration) {
        super(name, status, description, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        String result = "Subtask{" + "epicId=" + epicId + ", name='" + super.getName() + '\'';

        if (super.getDescription() == null) {
            result += ", description=null";
        } else {
            result += ", description.length=" + super.getDescription().length();
        }

        return result += ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                ", startTime=" + super.getStartTime() +
                ", duration=" + super.getDuration() +
                ", endTime=" + super.getEndTime() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}