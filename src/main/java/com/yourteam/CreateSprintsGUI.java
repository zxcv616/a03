import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * GUI form for creating a new sprint scoped to a specific project.
 * Lets the user pick stories from the project's backlog to assign to the sprint.
 *
 * @author Ivan Torriani
 * @version 1.0
 */
public class CreateSprintsGUI extends JFrame {

    private final Project project;
    private final DefaultTableModel sprintTableModel;

    private JTextField nameField;
    private JTextArea descriptionArea;
    private JList<String> storiesList;
    private DefaultListModel<String> storiesListModel;
    private JTextArea outputArea;

    private static int nextId = 1;

    /**
     * @param project         the project this sprint belongs to
     * @param sprintTableModel the table in ProjectDetailFrame to update on creation
     */
    public CreateSprintsGUI(Project project, DefaultTableModel sprintTableModel) {
        this.project = project;
        this.sprintTableModel = sprintTableModel;

        setTitle("New Sprint for: " + project.getName());
        setSize(500, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(0, 1, 5, 5));

        inputPanel.add(new JLabel("Sprint Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(4, 20);
        inputPanel.add(new JScrollPane(descriptionArea));

        // Stories come from this project's backlog
        inputPanel.add(new JLabel("Assign Stories (hold Cmd/Ctrl to multi-select):"));
        storiesListModel = new DefaultListModel<>();
        for (Stories s : project.getStory()) {
            storiesListModel.addElement(s.getSubjectLine());
        }
        storiesList = new JList<>(storiesListModel);
        storiesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        storiesList.setVisibleRowCount(4);
        inputPanel.add(new JScrollPane(storiesList));

        JButton createButton = new JButton("Create Sprint");
        inputPanel.add(createButton);

        inputPanel.add(new JLabel("Output:"));
        outputArea = new JTextArea(4, 20);
        outputArea.setEditable(false);
        inputPanel.add(new JScrollPane(outputArea));

        createButton.addActionListener(e -> handleCreateSprint());

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void handleCreateSprint() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (name.isEmpty()) {
            outputArea.setText("Error: Sprint name cannot be empty.");
            return;
        }

        int id = nextId++;
        Sprints sprint = new Sprints(id, name, description);

        // Attach selected stories from this project's backlog
        List<String> selectedNames = storiesList.getSelectedValuesList();
        for (Stories s : project.getStory()) {
            if (selectedNames.contains(s.getSubjectLine())) {
                sprint.addUserStory(s);
            }
        }

        // Save to project and repository
        project.addUserSprint(sprint);
        ProjectRepository.getInstance().addSprint(sprint);

        // Update the sprint table in ProjectDetailFrame
        sprintTableModel.addRow(new Object[]{
            sprint.getId(), sprint.getName(), sprint.getDescription(), sprint.getStory().size()
        });

        outputArea.setText(
            "Sprint Created!\n" +
            "Name:    " + sprint.getName() + "\n" +
            "Stories: " + sprint.getStory().size()
        );

        nameField.setText("");
        descriptionArea.setText("");
        storiesList.clearSelection();
    }
}
