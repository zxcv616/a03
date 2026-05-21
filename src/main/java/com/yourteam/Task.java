package com.yourteam;
/**
 * Represents a task with a name, user assignment, and value.
 *
 * @author Anthony Soto
 * @version 1.0
 */
public class Task {

    private String taskName;
    private String userAssignment;
    private int taskValue;

    public Task(String taskName, String userAssignment, int taskValue) {
        this.taskName = taskName;
        this.userAssignment = userAssignment;
        this.taskValue = taskValue;
    }

    public void   setTaskName(String newTaskName)         { this.taskName = newTaskName; }
    public void   setUserAssignment(String newAssignment) { this.userAssignment = newAssignment; }
    public void   setTaskValue(int newTaskValue)          { this.taskValue = newTaskValue; }
    public String getTaskName()                           { return taskName; }
    public String getUserAssignment()                     { return userAssignment; }
    public int    getTaskValue()                          { return taskValue; }
}
