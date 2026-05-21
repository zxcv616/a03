package com.yourteam;
import javax.swing.*;
import java.awt.*;

/**
 * Main application window for the project management tool.
 * Hosts a "New Project" button, the live project list panel (top),
 * and the product backlog panel (bottom).
 * The list updates automatically via the Observer pattern when a project is created.
 *
 * @author Nico Yenikomishian
 * @version 1.0
 */
public class MainApp extends JFrame {

    /**
     * Builds the main window with a toolbar, project list, and backlog panel.
     */
    public MainApp() {
        setTitle("Project Management");
        setSize(700, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton newProjectBtn = new JButton("+ New Project");
        newProjectBtn.addActionListener(e -> {
            CreateProjectGUI form = new CreateProjectGUI();
            form.setVisible(true);
        });
        toolbar.add(newProjectBtn);
        toolbar.add(new com.yourteam.taiga.TaigaLoader().createLoadButton());

        JSplitPane splitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            new ProjectListPanel(),
            new JPanel() // placeholder bottom panel — content lives inside project detail now
        );
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerLocation(300);

        root.add(toolbar, BorderLayout.NORTH);
        root.add(splitPane, BorderLayout.CENTER);

        add(root);
    }

    /**
     * Entry point for running the main application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }
}
