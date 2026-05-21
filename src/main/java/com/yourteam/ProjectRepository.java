import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Blackboard-style shared data repository for projects.
 * Implemented as a singleton so all parts of the application share one instance.
 * Observers registered here are notified automatically whenever a project is added.
 *
 * @author Matthew Wiecking
 * @version 1.0
 */
public class ProjectRepository {

    private static ProjectRepository instance;

    private final List<Project> projects = new ArrayList<>();
    private final List<Stories> stories = new ArrayList<>();
    private final Map<Integer, Sprints> sprints = new HashMap<>();
    private final List<ProjectObserver> observers = new ArrayList<>();

    private ProjectRepository() {}

    /**
     * Returns the single shared instance of this repository.
     *
     * @return the ProjectRepository singleton
     */
    public static ProjectRepository getInstance() {
        if (instance == null) {
            instance = new ProjectRepository();
        }
        return instance;
    }

    /**
     * Adds a project to the repository and notifies all registered observers.
     *
     * @param project the project to add
     */
    public void addProject(Project project) {
        projects.add(project);
        notifyObservers(project);
    }

    public List<Project> getProjects() {
        return Collections.unmodifiableList(projects);
    }

    public Project getProjectById(int id) {
        for (Project p : projects) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public void addStory(Stories story) {
        stories.add(story);
    }

    public List<Stories> getStories() {
        return Collections.unmodifiableList(stories);
    }

    public void addSprint(Sprints sprint) {
        sprints.put(sprint.getId(), sprint);
    }

    public Sprints getSprintById(int id) {
        return sprints.get(id);
    }

    /**
     * Registers an observer to be notified on project additions.
     *
     * @param observer the observer to add
     */
    public void addObserver(ProjectObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes a previously registered observer.
     *
     * @param observer the observer to remove
     */
    public void removeObserver(ProjectObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(Project project) {
        for (ProjectObserver observer : observers) {
            observer.onProjectAdded(project);
        }
    }
}
