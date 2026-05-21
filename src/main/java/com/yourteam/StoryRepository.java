import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton to hold all the current stories for when 
 * sprints are created. 
 * 
 *
 * @author Ivan Torriani
 * @version 1.0
 */
public class StoryRepository {

    //create an instance of the story repository
    private static StoryRepository instance;
    
    private final List<Stories> stories = new ArrayList<>();

    private StoryRepository() {}

    public static StoryRepository getInstance() {
        if (instance == null) {
            instance = new StoryRepository();
        }
        return instance;
    }

    public void addStory(Stories story) {
        stories.add(story);
    }

    public List<Stories> getStories() {
        return Collections.unmodifiableList(stories);
    }
}
