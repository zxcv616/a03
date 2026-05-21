import javax.swing.*;
import java.awt.*;

/**
 * GUI form for creating a new project.
 * Collects project name, description, type, and auth type from the user,
 * then constructs a Project object. Will connect to ProjectRepository
 * (Blackboard) in Task 2.
 *
 * @author Matthew Wiecking
 * @version 1.0
 */
public class CreateProjectGUI extends JFrame {

    private JTextField nameField;
    private JTextArea descriptionArea;
    private JComboBox<String> projTypeCombo;
    private JComboBox<String> authTypeCombo;
    private JTextArea outputArea;

    private static int nextId = 1;

    /**
     * Constructs and lays out the Create Project form.
     */
    public CreateProjectGUI() {
        setTitle("Create New Project");
        setSize(500, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(0, 1, 5, 5));

        inputPanel.add(new JLabel("Project Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(4, 20);
        inputPanel.add(new JScrollPane(descriptionArea));

        inputPanel.add(new JLabel("Project Type:"));
        projTypeCombo = new JComboBox<>(new String[]{"Scrum", "Kanban", "Waterfall"});
        inputPanel.add(projTypeCombo);

        inputPanel.add(new JLabel("Auth Type:"));
        authTypeCombo = new JComboBox<>(new String[]{"Public", "Private", "Protected"});
        inputPanel.add(authTypeCombo);

        JButton createButton = new JButton("Create Project");
        inputPanel.add(createButton);

        inputPanel.add(new JLabel("Output:"));
        outputArea = new JTextArea(5, 20);
        outputArea.setEditable(false);
        inputPanel.add(new JScrollPane(outputArea));

        createButton.addActionListener(e -> handleCreateProject());

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    /**
     * Validates input, creates a Project, and displays a confirmation.
     * Clears the form on success.
     */
    private void handleCreateProject() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String projType = (String) projTypeCombo.getSelectedItem();
        String authType = (String) authTypeCombo.getSelectedItem();

        if (name.isEmpty()) {
            outputArea.setText("Error: Project name cannot be empty.");
            return;
        }

        Project project = new Project(nextId++, name, description, projType, authType);

        ProjectRepository.getInstance().addProject(project);

        outputArea.setText(
            "Project Created Successfully!\n" +
            "ID:          " + project.getId() + "\n" +
            "Name:        " + project.getName() + "\n" +
            "Description: " + project.getDescription() + "\n" +
            "Type:        " + project.getProjType() + "\n" +
            "Auth:        " + project.getAuthType()
        );

        nameField.setText("");
        descriptionArea.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CreateProjectGUI gui = new CreateProjectGUI();
            gui.setVisible(true);
        });
    }
}
