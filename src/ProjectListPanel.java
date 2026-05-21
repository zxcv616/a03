import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Displays all projects. Double-clicking a row opens that project's detail window.
 *
 * @author Matthew Wiecking
 * @version 1.0
 */
public class ProjectListPanel extends JPanel implements ProjectObserver {

    private final DefaultTableModel tableModel;
    private final JTable table;

    public ProjectListPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Projects (double-click to open)"));

        String[] columns = {"ID", "Name", "Description", "Type", "Auth"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(3).setMaxWidth(80);
        table.getColumnModel().getColumn(4).setMaxWidth(80);

        // Double-click opens the project detail window
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row == -1) return;
                    int projectId = (int) tableModel.getValueAt(row, 0);
                    Project project = ProjectRepository.getInstance().getProjectById(projectId);
                    if (project != null) {
                        new ProjectDetailFrame(project).setVisible(true);
                    }
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        ProjectRepository.getInstance().addObserver(this);
        for (Project p : ProjectRepository.getInstance().getProjects()) {
            addRow(p);
        }
    }

    @Override
    public void onProjectAdded(Project project) {
        SwingUtilities.invokeLater(() -> addRow(project));
    }

    private void addRow(Project project) {
        tableModel.addRow(new Object[]{
            project.getId(),
            project.getName(),
            project.getDescription(),
            project.getProjType(),
            project.getAuthType()
        });
    }
}
