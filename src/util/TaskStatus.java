package util;

public enum TaskStatus {
    NEW,
    IN_PROGRESS,
    DONE;

    public boolean isInProgress() {
        return this == IN_PROGRESS;
    }
}
