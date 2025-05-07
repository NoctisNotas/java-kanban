public class Task {
    private String taskName;
    private String taskDescription;
    private int taskIdentifier;
    private TaskStatus taskStatus;

    public Task(String taskName, TaskStatus taskStatus, String taskDescription) {
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.taskDescription = taskDescription;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public int getTaskIdentifier() {
        return taskIdentifier;
    }

    public void setTaskIdentifier(int taskIdentifier) {
        this.taskIdentifier = taskIdentifier;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public String toString() {
        String result = "Task{" + "taskName='" + taskName + '\'';

        if (taskDescription == null) {
            result += ", taskDescription=null";
        } else {
            result += ", taskDescription.length=" + taskDescription.length();
        }

        return result += ", taskIdentifier=" + taskIdentifier +
                ", taskStatus=" + taskStatus + '}';
    }
}
