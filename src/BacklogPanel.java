/**
 * Panel that displays the product backlog — a list of user stories.
 * Shown on the bottom half of the main dashboard.
 * Stories can be added via a dialog that prompts for description, value, and assigned user.
 *
 * @author Ivan Torriani
 * @version 1.0
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class BacklogPanel extends JPanel {

    private DefaultTableModel tableModel;
    private ArrayList<Stories> storyList = new ArrayList<>();

    /**
     * Constructs the BacklogPanel with a table and an "Add Story" button.
     */
    public BacklogPanel() {
        setLayout(new BorderLayout());

        // Create column names for table
        String[] columns = {"Name", "Description", "Value", "Assignment"};

        // Create the table
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);

        // Add it to the panel
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Create a new button
        JButton newStoryButton = new JButton("Create New Story");
        add(newStoryButton, BorderLayout.SOUTH);

        // Listener for button press to open new story popup
        newStoryButton.addActionListener(e -> newStoryPopup());
    }

    /**
     * Opens a popup dialog to create and add a new story to the backlog.
     */
    private void newStoryPopup() {
        /*
         * Create a popup window living within the panel that
         * allows you to add new stories when button is clicked
         */
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Story", true);
        dialog.setLayout(new GridLayout(0, 2, 5, 5));
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        // Create the fields
        dialog.add(new JLabel("Name:"));
        JTextField field1 = new JTextField();
        dialog.add(field1);

        dialog.add(new JLabel("Description:"));
        JTextField field2 = new JTextField();
        dialog.add(field2);

        dialog.add(new JLabel("Value:"));
        JTextField field3 = new JTextField();
        dialog.add(field3);

        dialog.add(new JLabel("User Assignment:"));
        JTextField field4 = new JTextField();
        dialog.add(field4);

        // Button to confirm and add the story
        JButton addStory = new JButton("Add Story to Backlog");
        dialog.add(new JLabel()); // spacer
        dialog.add(addStory);

        // Action listener for when addStory button is pressed
        addStory.addActionListener(e -> {
            //create new story based on entries
            Stories story = new Stories(
                field1.getText(),
                field2.getText(),
                Integer.parseInt(field3.getText()),
                field4.getText()
            );
            //add story to the list of stories
            storyList.add(story);
            ProjectRepository.getInstance().addStory(story);
            
            //add it to the table
            tableModel.addRow(new Object[]{
                story.getSubjectLine(),
                story.getDescription(),
                story.getValue(),
                story.getAssignedUser()
            });
            dialog.dispose();
        });

        dialog.setVisible(true);
    }
}
