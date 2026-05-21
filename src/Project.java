/**
 * Represents a project with stories, tasks, and sprints.
 * Projects have an ID, name, description, type, and auth type.
 *
 * @author Ivan Torriani
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.List;

public class Project {
    private int id;
    private String name;
    private String description;
    private String projType;
    private String authType;
    private List<Stories> userStories;
    private List<Task> userTasks;
    private List<Sprints> userSprints;

    public Project(int id, String name, String description, String projType, String authType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.projType = projType;
        this.authType = authType;
        this.userStories = new ArrayList<Stories>();
        this.userTasks = new ArrayList<Task>();
        this.userSprints = new ArrayList<Sprints>();
    }

    public void addUserStory(Stories s) {
        this.userStories.add(s);
    }

    public void addUserTask(Task t) {
        this.userTasks.add(t);
    }

    public void addUserSprint(Sprints sp) {
        this.userSprints.add(sp);
    }

    public List<Stories> getStory() {
        return this.userStories;
    }

    public List<Task> getTask() {
        return this.userTasks;
    }

    public List<Sprints> getSprint() {
        return this.userSprints;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getProjectName() { return name; }
    public String getDescription() { return description; }
    public String getProjType() { return projType; }
    public String getAuthType() { return authType; }
}