import java.util.List;
import java.util.ArrayList;

/**
 * A panel that creates a new sprint.
 * 
 *
 * @author Ivan Torriani
 * @version 1.0
 */

public class Sprints {
    private int id;
    private String name;
    private String description;
    private List<Stories> userStories;
    private List<Task> userTasks;

    public Sprints(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userStories = new ArrayList<Stories>();
        this.userTasks = new ArrayList<Task>();
    }

    public void addUserStory(Stories s) {
        this.userStories.add(s);
    }

    public void addUserTask(Task t) {
        this.userTasks.add(t);
    }

    public List<Stories> getStory() {
        return this.userStories;
    }

    public List<Task> getTask() {
        return this.userTasks;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}
