/**
 * Represents a user in the system with associated projects.
 * Users have a name, email, and list of projects.
 *
 * @author Anthony Soto
 * @version 1.0
 */

import java.util.*;

public class User {
    // User class to represent a user in the system
    private String name;
    private String email;
    private LinkedList<Project> projects = new LinkedList<>();    


    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    // Method to add a project to the user's list of projects
    public void addProjectToUser(Project project) {
        projects.add(project);
    }

    // Method to get the list of projects associated with the user
    public LinkedList<Project> getProjects() {
        return projects;
    }

    // Method to remove a project from the user's list of projects
    public void removeUserProject(Project project) {
        projects.remove(project);
    }

    // Method to display user's projects
    public void displayUserProjects() {
        System.out.println("Projects for user: " + this.name);
        for (Project project : projects) {
            System.out.println("- " + project);
        }
    }


    // Override toString for better user representation
    @Override
    public String toString() {
        return "User{name='" + name + "', email='" + email + "'}";
    }
}
