import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

/**
 * Window display for a new project.
 * that pops up when double clicked
 *
 * @author Ivan Torriani
 * @version 1.0
 */
public class ProjectDetailFrame extends JFrame {

    //create project instance
    private Project project;

    //create tables for sprints and stories
    private DefaultTableModel sprintTableModel;
    private DefaultTableModel storyTableModel;

    public ProjectDetailFrame(Project project) {
        //refer to project instance
        this.project = project;

        //title the popup based on project title
        setTitle("Project: " + project.getName());
        setSize(650, 600);

        //closing operation
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);


        //debugging formatting errors
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //__________________________________________-
        /*
        Sprint panel 
        */
        //Create sprint panel on the top of the popup
        JPanel sprintPanel = new JPanel(new BorderLayout(5, 5));
        sprintPanel.setBorder(BorderFactory.createTitledBorder("Sprints"));

        //define the sprint columns
        String[] sprintCols = {"ID", "Name", "Description", "Stories"};

        // initialize the model
        sprintTableModel = new DefaultTableModel(sprintCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        ///create the table
        JTable sprintTable = new JTable(sprintTableModel);

        //formatting
        sprintTable.setFillsViewportHeight(true);
        sprintTable.getColumnModel().getColumn(0).setMaxWidth(40);
        sprintTable.getColumnModel().getColumn(3).setMaxWidth(60);

        // Double-click a sprint to see its stories
        sprintTable.addMouseListener(new MouseAdapter() {
            @Override //debug overide
            public void mouseClicked(MouseEvent e) {
                //for when there are two clicks
                if (e.getClickCount() == 2) {
                    //get the row
                    int row = sprintTable.getSelectedRow();
                    //get sprint id
                    int sprintId = (int) sprintTableModel.getValueAt(row, 0);

                    //debugging
                    Sprints sprint = ProjectRepository.getInstance().getSprintById(sprintId);
                    if (sprint != null) openSprintDetail(sprint);
                }
            }
        });

        //add sprint
        sprintPanel.add(new JScrollPane(sprintTable), BorderLayout.CENTER);

        JButton addSprintBtn = new JButton("+ New Sprint");
        addSprintBtn.addActionListener(e -> {
            CreateSprintsGUI form = new CreateSprintsGUI(project, sprintTableModel);
            form.setVisible(true);
        });
        sprintPanel.add(addSprintBtn, BorderLayout.SOUTH);
        

        //__________________________________________-
        /*
        Backlog panel for stories
        */
        JPanel storyPanel = new JPanel(new BorderLayout(5, 5));
        storyPanel.setBorder(BorderFactory.createTitledBorder("Backlog Stories"));

        String[] storyCols = {"Name", "Description", "Value", "Assigned To"};
        storyTableModel = new DefaultTableModel(storyCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable storyTable = new JTable(storyTableModel);
        storyTable.setFillsViewportHeight(true);
        storyPanel.add(new JScrollPane(storyTable), BorderLayout.CENTER);

        JButton addStoryBtn = new JButton("+ New Story");
        addStoryBtn.addActionListener(e -> openNewStoryDialog());
        storyPanel.add(addStoryBtn, BorderLayout.SOUTH);

        // Populate from the project's existing data
        for (Sprints s : project.getSprint()) {
            addSprintRow(s);
        }
        for (Stories s : project.getStory()) {
            addStoryRow(s);
        }

        //formatting
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sprintPanel, storyPanel);
        split.setResizeWeight(0.5);
        split.setDividerLocation(250);

        root.add(split, BorderLayout.CENTER);
        add(root);
    }

    
    private void openSprintDetail(Sprints sprint) {
        /*
        Functionality to open sprint related information
        */

        //title sprint detail based on name
        JFrame detail = new JFrame("Sprint: " + sprint.getName());

        //formatting
        detail.setSize(450, 350);
        detail.setLocationRelativeTo(this);
        detail.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //create new panel 
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Stories in \"" + sprint.getName() + "\":"), BorderLayout.NORTH);

        //format based on story characteristics
        String[] cols = {"Name", "Description", "Value", "Assigned To"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Stories s : sprint.getStory()) {
            model.addRow(new Object[]{s.getSubjectLine(), s.getDescription(), s.getValue(), s.getAssignedUser()});
        }
        JTable t = new JTable(model);
        t.setFillsViewportHeight(true);
        panel.add(new JScrollPane(t), BorderLayout.CENTER);

        detail.add(panel);
        detail.setVisible(true);
    }

    /** Inline dialog to add a story directly to this project's backlog. */
    private void openNewStoryDialog() {
        JDialog dialog = new JDialog(this, "New Story", true);
        dialog.setLayout(new GridLayout(0, 2, 5, 5));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        dialog.add(new JLabel("Name:")); JTextField f1 = new JTextField(); dialog.add(f1);
        dialog.add(new JLabel("Description:")); JTextField f2 = new JTextField(); dialog.add(f2);
        dialog.add(new JLabel("Value:")); JTextField f3 = new JTextField(); dialog.add(f3);
        dialog.add(new JLabel("User Assignment:")); JTextField f4 = new JTextField(); dialog.add(f4);
        dialog.add(new JLabel("Tasks (comma-separated):")); JTextField f5 = new JTextField(); dialog.add(f5);

        JButton add = new JButton("Add Story");
        dialog.add(new JLabel());
        dialog.add(add);

        add.addActionListener(e -> {
            Stories story = new Stories(f1.getText(), f2.getText(),
                    Integer.parseInt(f3.getText()), f4.getText());
          
           //get tasks by comma seperated values
            LinkedList<String> taskList = new LinkedList<>();
            
            //iterate through the string
            for (String t : f5.getText().split(",")) {
                //debug trim
                String trimmed = t.trim();
                //add totaskList
                if (!trimmed.isEmpty()) taskList.add(trimmed);
            }
            //add 
            story.setTasks(taskList);
            project.addUserStory(story);
            ProjectRepository.getInstance().addStory(story);
            addStoryRow(story);
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    private void addSprintRow(Sprints s) {
        sprintTableModel.addRow(new Object[]{s.getId(), s.getName(), s.getDescription(), s.getStory().size()});
    }

    private void addStoryRow(Stories s) {
        storyTableModel.addRow(new Object[]{s.getSubjectLine(), s.getDescription(), s.getValue(), s.getAssignedUser()});
    }
}
