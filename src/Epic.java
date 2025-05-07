import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId;

    public Epic(String taskName, String taskDescription) {
        super(taskName, TaskStatus.NEW, taskDescription);
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
    public String toString() {
        String result = "Epic{" + "subtasksId=" + subtasksId + ", taskName='" + super.getTaskName() + '\'';

        if (super.getTaskDescription() == null) {
            result += ", taskDescription=null";
        } else {
            result += ", taskDescription.length=" + super.getTaskDescription().length();
        }

        return result += ", taskIdentifier=" + super.getTaskIdentifier() + ", taskStatus=" + super.getTaskStatus() + '}';
    }
}
