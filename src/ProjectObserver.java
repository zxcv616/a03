/**
 * Observer interface for receiving notifications when a project is added
 * to the ProjectRepository (Blackboard).
 *
 * @author Matthew Wiecking
 * @version 1.0
 */
public interface ProjectObserver {

    /**
     * Called by ProjectRepository whenever a new project is added.
     *
     * @param project the newly added project
     */
    void onProjectAdded(Project project);
}
