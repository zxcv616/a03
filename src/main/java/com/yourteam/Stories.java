package com.yourteam;
/**
 * Represents a user story with a subject line, description, attachments, position, value, and assigned user.
 *
 * @author Ivan Torriani
 * @version 1.0
 */

import java.util.LinkedList;

public class Stories {

    private String subjectLine;
    private String description;
    private LinkedList<String> attachments = new LinkedList<>();
    private LinkedList<String> tasks = new LinkedList<>();
    private boolean position;
    private int value;
    private String assignedUser;



    public Stories(String subjectLine, String description, int value, String assignedUser) {
        this.subjectLine = subjectLine;
        this.description = description;
        this.value = value;
        this.assignedUser = assignedUser;
        this.attachments = new LinkedList<>();
        this.position = false;
    }


    public void setSubjectLine(String newSubjectLine) {
        this.subjectLine = newSubjectLine;
    }


    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    
    public void setPosition(boolean newPosition) {
        this.position = newPosition;
    }

    public void setAttachments(LinkedList<String> newAttachments) {
        this.attachments = newAttachments;
    }

    public void setValue(int value) {
        this.value = value;
    }


    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }


    public String getSubjectLine() {
        return subjectLine;
    }


    public String getDescription() {
        return description;
    }


    public LinkedList<String> getAttachments() {
        return attachments;
    }


    public boolean getPosition() {
        return position;
    }


    public int getValue() {
        return value;
    }


    public String getAssignedUser() {
        return assignedUser;
    }

    public void setTasks(LinkedList<String> tasks) {
        this.tasks = tasks;
    }

    public LinkedList<String> getTasks() {
        return tasks;
    }
}
