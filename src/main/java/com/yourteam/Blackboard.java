/**
 * Blackboard for organizing tasks into different workflow sections.
 * Sections include: New, In Progress, Ready for Test, Closed, Needs Info.
 *
 * @author Ivan Torriani
 * @version 1.0
 */

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class Blackboard {

    //create hashmap of different sections
    private Map<String, List<Task>> taskBoardSections = new HashMap<>();

    public Blackboard() {

        //Initialize the taskboard with these sections
        taskBoardSections.put("New", new ArrayList<>());

        taskBoardSections.put("In Progress", new ArrayList<>());

        taskBoardSections.put("Ready for Test", new ArrayList<>());

        taskBoardSections.put("Closed", new ArrayList<>());

        taskBoardSections.put("Needs Info", new ArrayList<>());
    }

    public void addNewTask(String taskName, String userAssignment, int taskValue)
    {
        /*
        Description: Create a new task and add it to the new task section to start with
        */
        Task newTask = new Task(taskName, userAssignment, taskValue); //create a new task

        taskBoardSections.get("New").add(newTask); //add new task to task section
    }

    public void reorganizeTask(String oldSection, String newSection)
    {
        /*
        Description: Primitive functionality to move a task from the old 
        section to the new section. Primitive because I'm only moving the first element
        from that old section to a new section. 
        */

        Task taskToMove = taskBoardSections.get(oldSection).get(0); // get the first element

        taskBoardSections.get(newSection).add(taskToMove); // add task
    }
}
