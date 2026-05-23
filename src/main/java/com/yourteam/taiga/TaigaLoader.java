package com.yourteam.taiga;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridLayout;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.yourteam.Blackboard;
import com.yourteam.Project;
import com.yourteam.Stories;
import com.yourteam.Sprints;
import com.yourteam.Task;

/**
 * Loads Taiga data into the local application by translating information.
 *
 * @author Ivan Torriani
 * @version 1.0
 */
public class TaigaLoader {

    /**
     * Creates and returns a "Load from Taiga" button.
     * Add your loading logic inside the action listener body.
     *
     * @return the configured JButton
     */


    public JButton createLoadButton() {
        JButton btn = new JButton("Load from Taiga");

        btn.addActionListener(e -> {
            try {
                // prompt for Taiga credentials
                JTextField userField = new JTextField();
                JPasswordField passField = new JPasswordField();
                JPanel credPanel = new JPanel(new GridLayout(0, 1, 5, 5));
                credPanel.add(new JLabel("Taiga Username / Email:"));
                credPanel.add(userField);
                credPanel.add(new JLabel("Password:"));
                credPanel.add(passField);

                int result = JOptionPane.showConfirmDialog(null, credPanel,
                    "Connect to Taiga", JOptionPane.OK_CANCEL_OPTION);
                if (result != JOptionPane.OK_OPTION) return;

                String username = userField.getText().trim();
                String password = new String(passField.getPassword());

                // scrape fresh data from Taiga
                TaigaClient client = new TaigaClient("https://api.taiga.io");
                String cleanedFile = client.scrape(username, password);

                // load the cleaned output into the local app
                translateTaiga(cleanedFile);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        return btn;
    }

    public void translateTaiga(String jsonUrl) throws Exception
    {
        //access the project repo singleton containing all values
        Blackboard repo = Blackboard.getInstance();

        // set up iteration of JSON file
        String content = new String(Files.readAllBytes(Paths.get(jsonUrl)));
        JSONArray projects = new JSONArray(content);

        // iterate through each project
        for (int i = 0; i < projects.length(); i++) {
            JSONObject projectJson = projects.getJSONObject(i);  // renamed to avoid collision
            String projectName = projectJson.getString("project_name");
            int    projectId   = projectJson.getInt("project_id");

            //add project to singleton
            Project project = new Project(projectId, projectName, "n/a", "n/a", "n/a");
            repo.addProject(project);  // fixed: added semicolon

            // iterate through stories
            JSONArray stories = projectJson.getJSONArray("stories");
            for (int j = 0; j < stories.length(); j++) {
                JSONObject story = stories.getJSONObject(j);

                String storySubject    = story.optString("subject");
                Object storyAssignedTo = story.isNull("assigned_to") ? null : story.get("assigned_to");
                Object storyPoints     = story.isNull("points") ? null : story.get("points");

                //add stories to corresponding project and singleton
                Stories storyObject = new Stories(storySubject, "n/a", storyPoints == null ? 0 : ((Number) storyPoints).intValue(), storyAssignedTo == null ? "" : storyAssignedTo.toString());
                project.addUserStory(storyObject);  // fixed: storyObject not story
                repo.addStory(storyObject);          // fixed: storyObject not story
            }

            // iterate through tasks
            JSONArray tasks = projectJson.getJSONArray("tasks");
            for (int j = 0; j < tasks.length(); j++) {
                JSONObject task = tasks.getJSONObject(j);

                String taskSubject    = task.optString("subject");
                Object taskAssignedTo = task.isNull("assigned_to") ? null : task.get("assigned_to");

                //Add the tasks to the corresponding singleton and repo
                Task taskObject = new Task(taskSubject, taskAssignedTo == null ? "" : taskAssignedTo.toString(), 0);
                project.addUserTask(taskObject);
            }

            // iterate through sprints
            JSONArray sprints = projectJson.getJSONArray("sprints");
            for (int j = 0; j < sprints.length(); j++) {
                JSONObject sprint = sprints.getJSONObject(j);

                String sprintName            = sprint.optString("name");
                String sprintEstimatedStart  = sprint.optString("estimated_start");
                String sprintEstimatedFinish = sprint.optString("estimated_finish");

                //Add the sprint objects to the new repo
                Sprints sprintObject = new Sprints(j, sprintName, sprintEstimatedStart + " to " + sprintEstimatedFinish);
                project.addUserSprint(sprintObject);
                repo.addSprint(sprintObject);
            }

            JSONArray members = projectJson.getJSONArray("members");
            for (int j = 0; j < members.length(); j++) {
                members.getJSONObject(j);
            }
        }
    }
}
