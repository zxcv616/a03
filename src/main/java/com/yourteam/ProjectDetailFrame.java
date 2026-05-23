package com.yourteam;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
    private JTable storyTable;

    public ProjectDetailFrame(Project project) {
        //refer to project instance
        this.project = project;

        //title the popup based on project title
        setTitle("Project: " + project.getName());
        setSize(650, 600);

        //closing operation
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);


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
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = sprintTable.getSelectedRow();
                    int sprintId = (int) sprintTableModel.getValueAt(row, 0);
                    Sprints sprint = Blackboard.getInstance().getSprintById(sprintId);
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
        storyTable = new JTable(storyTableModel);
        storyTable.setFillsViewportHeight(true);
        storyPanel.add(new JScrollPane(storyTable), BorderLayout.CENTER);

        JButton addStoryBtn = new JButton("+ New Story");
        addStoryBtn.addActionListener(e -> openNewStoryDialog());

        JButton aiReviewBtn = new JButton("AI Review");
        aiReviewBtn.addActionListener(e -> openAIReview());

        JPanel storyBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        storyBtnRow.add(addStoryBtn);
        storyBtnRow.add(aiReviewBtn);
        storyPanel.add(storyBtnRow, BorderLayout.SOUTH);

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

    private void openAIReview() {
        int row = storyTable.getSelectedRow();
        com.yourteam.groq.GroqPanel panel = new com.yourteam.groq.GroqPanel();

        if (row != -1 && row < project.getStory().size()) {
            panel.setStory(project.getStory().get(row));
        }

        JFrame frame = new JFrame("AI Review — " + project.getName());
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(this);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        frame.setVisible(true);
    }

    private void openSprintDetail(Sprints sprint) {
        JFrame detail = new JFrame("Sprint: " + sprint.getName());
        detail.setSize(700, 400);
        detail.setLocationRelativeTo(this);
        detail.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Stories in \"" + sprint.getName() + "\":"), BorderLayout.NORTH);

        String[] cols = {"Name", "Assigned To", "New", "In Progress", "Ready for Testing", "Complete"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Stories s : sprint.getStory()) {
            model.addRow(new Object[]{
                s.getSubjectLine(),
                s.getAssignedUser(),
                "☐", "☐", "☐", "☐"
            });
        }
        JTable t = new JTable(model);
        t.setFillsViewportHeight(true);
        t.setRowHeight(24);

        // clicking a status column toggles the checkbox for that row
        t.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = t.rowAtPoint(e.getPoint());
                int col = t.columnAtPoint(e.getPoint());
                if (col >= 2 && col <= 5) {
                    // clear all status columns for this row, then check the clicked one
                    for (int c = 2; c <= 5; c++) {
                        model.setValueAt("☐", row, c);
                    }
                    model.setValueAt("☑", row, col);
                }
            }
        });

        panel.add(new JScrollPane(t), BorderLayout.CENTER);
        detail.add(panel);
        detail.setVisible(true);
    }

    /** Inline dialog to add a story, with a live Groq AI panel alongside the form. */
    private void openNewStoryDialog() {
        JDialog dialog = new JDialog(this, "New Story", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(820, 360);
        dialog.setLocationRelativeTo(this);

        // Story creation form
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        formPanel.add(new JLabel("Name:")); JTextField f1 = new JTextField(); formPanel.add(f1);
        formPanel.add(new JLabel("Description:")); JTextField f2 = new JTextField(); formPanel.add(f2);
        formPanel.add(new JLabel("Value:")); JTextField f3 = new JTextField(); formPanel.add(f3);
        formPanel.add(new JLabel("User Assignment:")); JTextField f4 = new JTextField(); formPanel.add(f4);
        formPanel.add(new JLabel("Tasks (comma-separated):")); JTextField f5 = new JTextField(); formPanel.add(f5);

        JButton add = new JButton("Add Story");
        formPanel.add(new JLabel());
        formPanel.add(add);

        // Groq panel beside the form — updates live as the user types
        com.yourteam.groq.GroqPanel groqPanel = new com.yourteam.groq.GroqPanel();

        DocumentListener liveUpdate = new DocumentListener() {
            private void update() {
                groqPanel.setStoryText(
                    "Title: "       + f1.getText() + "\n" +
                    "Description: " + f2.getText() + "\n" +
                    "Value: "       + f3.getText() + "\n" +
                    "Assigned To: " + f4.getText()
                );
            }
            public void insertUpdate(DocumentEvent e)  { update(); }
            public void removeUpdate(DocumentEvent e)  { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        };
        f1.getDocument().addDocumentListener(liveUpdate);
        f2.getDocument().addDocumentListener(liveUpdate);
        f3.getDocument().addDocumentListener(liveUpdate);
        f4.getDocument().addDocumentListener(liveUpdate);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, groqPanel);
        split.setResizeWeight(0.4);
        split.setDividerLocation(330);

        add.addActionListener(e -> {
            Stories story = new Stories(f1.getText(), f2.getText(),
                    Integer.parseInt(f3.getText()), f4.getText());
            LinkedList<String> taskList = new LinkedList<>();
            for (String t : f5.getText().split(",")) {
                String trimmed = t.trim();
                if (!trimmed.isEmpty()) taskList.add(trimmed);
            }
            story.setTasks(taskList);
            project.addUserStory(story);
            Blackboard.getInstance().addStory(story);
            addStoryRow(story);
            dialog.dispose();
        });

        dialog.add(split, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void addSprintRow(Sprints s) {
        sprintTableModel.addRow(new Object[]{s.getId(), s.getName(), s.getDescription(), s.getStory().size()});
    }

    private void addStoryRow(Stories s) {
        storyTableModel.addRow(new Object[]{s.getSubjectLine(), s.getDescription(), s.getValue(), s.getAssignedUser()});
    }
}
