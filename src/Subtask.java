public class Subtask extends Task {
    private int epicId;

    public Subtask(String taskName, TaskStatus taskStatus, String taskDescription, int epicId) {
        super(taskName, taskStatus, taskDescription);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        String result = "Subtask{" + "epicId=" + epicId + ", taskName='" + super.getTaskName() + '\'';

        if (super.getTaskDescription() == null) {
            result += ", taskDescription=null";
        } else {
            result += ", taskDescription.length=" + super.getTaskDescription().length();
        }

        return result += ", taskIdentifier=" + super.getTaskIdentifier() + ", taskStatus=" + super.getTaskStatus() + '}';
    }
}
