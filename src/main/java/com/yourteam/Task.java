/**
 * GUI for creating and managing Tasks.
 * A task has a name, user assignment, and value.
 *
 * @author Anthony Soto
 * @version 1.0
 */

//imports
import javax.swing.*;
import java.awt.*;


public class Task extends JFrame {


    //initialize variables

    private String taskName;
    
    private String userAssignment; 

    private int taskValue; 

    private JTextField taskNameField;
    
    private JTextField userAssignmentField;

    private JTextField taskValueField;

    private JTextArea outputArea;




    public Task(String taskName, String userAssignment, int taskValue) {
        this.taskName = taskName;
        this.userAssignment = userAssignment; 
        this.taskValue = taskValue;
    }


    /**
     * Constructs the Task GUI window with input fields and a create button.
     */
    public Task() {
        setTitle("Task GUI");

        setSize(500, 400);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);


        JPanel mainPanel = new JPanel();

        mainPanel.setLayout(new BorderLayout(10, 10));


        JPanel inputPanel = new JPanel();

        inputPanel.setLayout(new GridLayout(0, 1, 5, 5));


        JLabel taskNameLabel = new JLabel("Enter Task Name: ");

        taskNameField = new JTextField();


        JLabel userAssignmentLabel = new JLabel("Enter User Assignment: ");

        userAssignmentField = new JTextField();


        JLabel taskValueLabel = new JLabel("Enter Task Value: ");

        taskValueField = new JTextField();


        JButton createButton = new JButton("Create Task");


        outputArea = new JTextArea(8, 20);

        outputArea.setEditable(false);

        JScrollPane outputScrollPane = new JScrollPane(outputArea);


        createButton.addActionListener(e -> {
            String newTaskName = taskNameField.getText();

            String newUserAssignment = userAssignmentField.getText();

            int newTaskValue = 0;


            try {
                newTaskValue = Integer.parseInt(taskValueField.getText());

                setTaskName(newTaskName);

                setUserAssignment(newUserAssignment);

                setTaskValue(newTaskValue);


                outputArea.setText(
                    "Task Name: " + getTaskName() +
                    "\nUser Assignment: " + getUserAssignment() +
                    "\nTask Value: " + getTaskValue()
                );
            }
            catch (NumberFormatException error) {
                outputArea.setText("Task Value must be a number.");
            }
        });


        inputPanel.add(taskNameLabel);

        inputPanel.add(taskNameField);

        inputPanel.add(userAssignmentLabel);

        inputPanel.add(userAssignmentField);

        inputPanel.add(taskValueLabel);

        inputPanel.add(taskValueField);

        inputPanel.add(createButton);

        inputPanel.add(new JLabel("Output:"));

        inputPanel.add(outputScrollPane);


        mainPanel.add(inputPanel, BorderLayout.CENTER);


        add(mainPanel);
    }

    

    public void setTaskName(String newTaskName) {
        this.taskName = newTaskName;
    }


    public void setUserAssignment(String newUserAssignment) {
        this.userAssignment = newUserAssignment;
    }


    public void setTaskValue(int newTaskValue) {
        this.taskValue = newTaskValue;
    }


    public String getTaskName() {
        return taskName;
    }


    public String getUserAssignment() {
        return userAssignment;
    }


    public int getTaskValue() {
        return taskValue;
    }
    



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Task gui = new Task();

            gui.setVisible(true);
        });
    }
}