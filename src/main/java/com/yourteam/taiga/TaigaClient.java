package com.yourteam.taiga;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all HTTP communication with the Taiga REST API.
 * Returns raw JSON strings — no parsing happens here.
 *
 * @author Ivan Torriani
 * @version 1.0
 */
public class TaigaClient {

    //variables for url
    private String baseUrl;
    private OkHttpClient httpClient;

    //constructor
    public TaigaClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = new OkHttpClient();
    }

    public TaigaAuthToken authenticateUser(String username, String password) throws IOException {
        //create the full url and json
        String fullUrl = baseUrl + "/api/v1/auth";
        String json = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\",\"type\":\"normal\"}";

        //api setup
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder().url(fullUrl).post(body).build();

        try (Response response = httpClient.newCall(request).execute()) {
            JSONObject responseJson = new JSONObject(response.body().string());
            return new TaigaAuthToken(responseJson.getString("auth_token"));
        }
    }

    //Get all Taiga Project Related Info
    public String fetchProjects(TaigaAuthToken token) throws IOException {
        return get(baseUrl + "/api/v1/projects?member=925139", token);
    }

    public String fetchUserStories(TaigaAuthToken token, int projectId) throws IOException {
        return get(baseUrl + "/api/v1/userstories?project=" + projectId, token);
    }

    public String fetchTasks(TaigaAuthToken token, int projectId) throws IOException {
        return get(baseUrl + "/api/v1/tasks?project=" + projectId, token);
    }

    public String fetchSprints(TaigaAuthToken token, int projectId) throws IOException {
        return get(baseUrl + "/api/v1/milestones?project=" + projectId, token);
    }

    public String fetchMembers(TaigaAuthToken token, int projectId) throws IOException {
        return get(baseUrl + "/api/v1/memberships?project=" + projectId, token);
    }

    private String get(String url, TaigaAuthToken token) throws IOException {
        Request request = new Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer " + token.getToken())
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public List<String[]> findProjects(String jsonUrl) throws Exception
    {
        //List of projectName + projectId pairs
        List<String[]> projectPairs = new ArrayList<>();

        //read file and parse as JSONArray (projects response is an array)
        String content = new String(Files.readAllBytes(Paths.get(jsonUrl)));
        JSONArray jsonArray = new JSONArray(content);

        for (int i = 0; i < jsonArray.length(); i++) {
            /*
            Iterate through each project
            */
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String projectName = jsonObject.getString("name");
            int projectId = jsonObject.getInt("id");

            //add the pair of project name and project id to the list
            projectPairs.add(new String[]{projectName, String.valueOf(projectId)});
        }

        return projectPairs; //return the list of project names and project id
    }

    public void cleanJsonOutput(String jsonUrl, TaigaAuthToken token) throws Exception
    {
        List<String[]> projectPairings = findProjects(jsonUrl); //get the list of project names and ids

        //build a proper JSON array of projects with their data
        JSONArray outputArray = new JSONArray();

        for (String[] pair : projectPairings) {
            //iterate through all project pairings, saving the name and id
            String projectName = pair[0];
            int projectId = Integer.parseInt(pair[1]);

            //use the fetch methods to get the taiga relatedinfo
            JSONObject projectEntry = new JSONObject();
            projectEntry.put("project_name", projectName);
            projectEntry.put("project_id", projectId);
            projectEntry.put("stories", filterStories(fetchUserStories(token, projectId)));
            projectEntry.put("tasks",   filterTasks(fetchTasks(token, projectId)));
            projectEntry.put("sprints", filterSprints(fetchSprints(token, projectId)));
            projectEntry.put("members", filterMembers(fetchMembers(token, projectId)));

            //add it to the json object collection
            outputArray.put(projectEntry);
        }

        //write to cleaned_outputs with date as filename
        //use the date as the key
        java.time.LocalDate date = java.time.LocalDate.now();
        java.io.File outputDir = new java.io.File("src/main/java/com/yourteam/taiga/cleaned_outputs");
        outputDir.mkdirs();
        java.io.FileWriter writer = new java.io.FileWriter("src/main/java/com/yourteam/taiga/cleaned_outputs/" + date + ".json");
        writer.write(outputArray.toString(2));
        writer.close();
    }


    //generated functions to deal with JSON cleaning
    private JSONArray filterStories(String raw) {
        JSONArray input = new JSONArray(raw);
        JSONArray output = new JSONArray();
        for (int i = 0; i < input.length(); i++) {
            JSONObject s = input.getJSONObject(i);
            JSONObject filtered = new JSONObject();
            filtered.put("subject",     s.optString("subject"));
            filtered.put("status",      s.optString("status"));
            filtered.put("assigned_to", s.isNull("assigned_to") ? JSONObject.NULL : s.get("assigned_to"));
            filtered.put("points",      s.isNull("total_points") ? JSONObject.NULL : s.get("total_points"));
            output.put(filtered);
        }
        return output;
    }

    private JSONArray filterTasks(String raw) {
        JSONArray input = new JSONArray(raw);
        JSONArray output = new JSONArray();
        for (int i = 0; i < input.length(); i++) {
            JSONObject t = input.getJSONObject(i);
            JSONObject filtered = new JSONObject();
            filtered.put("subject",     t.optString("subject"));
            filtered.put("assigned_to", t.isNull("assigned_to") ? JSONObject.NULL : t.get("assigned_to"));
            output.put(filtered);
        }
        return output;
    }

    private JSONArray filterSprints(String raw) {
        JSONArray input = new JSONArray(raw);
        JSONArray output = new JSONArray();
        for (int i = 0; i < input.length(); i++) {
            JSONObject sp = input.getJSONObject(i);
            JSONObject filtered = new JSONObject();
            filtered.put("name",             sp.optString("name"));
            filtered.put("estimated_start",  sp.optString("estimated_start"));
            filtered.put("estimated_finish", sp.optString("estimated_finish"));
            output.put(filtered);
        }
        return output;
    }

    private JSONArray filterMembers(String raw) {
        JSONArray input = new JSONArray(raw);
        JSONArray output = new JSONArray();
        for (int i = 0; i < input.length(); i++) {
            JSONObject m = input.getJSONObject(i);
            JSONObject filtered = new JSONObject();
            filtered.put("username",  m.optString("username"));
            filtered.put("full_name", m.optString("full_name"));
            output.put(filtered);
        }
        return output;
    }

    /**
     * Runs the full scrape — fetches projects, writes raw output,
     * then cleans and enriches with stories, tasks, sprints, members.
     * Returns the path to the cleaned output file.
     */
    public String scrape(String username, String password) throws Exception {
        TaigaAuthToken token = authenticateUser(username, password);
        String projectsJson = fetchProjects(token);

        java.time.LocalDate date = java.time.LocalDate.now();
        java.io.File outputDir = new java.io.File("src/main/java/com/yourteam/taiga/project_outputs");
        outputDir.mkdirs();
        String projectsFile = "src/main/java/com/yourteam/taiga/project_outputs/" + date + ".json";
        java.io.FileWriter writer = new java.io.FileWriter(projectsFile);
        writer.write(projectsJson);
        writer.close();

        cleanJsonOutput(projectsFile, token);

        return "src/main/java/com/yourteam/taiga/cleaned_outputs/" + date + ".json";
    }

    //Hardcoding credentials at this point
    public static void main(String[] args) throws Exception {
        TaigaClient client = new TaigaClient("https://api.taiga.io");
        TaigaAuthToken token = client.authenticateUser("itorrian@calpoly.edu", "Ilikecamels1!");
        System.out.println("Token: " + token.getToken());

        String projectsJson = client.fetchProjects(token);

        // Write raw output json file
        java.time.LocalDate date = java.time.LocalDate.now();
        java.io.File outputDir = new java.io.File("src/main/java/com/yourteam/taiga/project_outputs");
        outputDir.mkdirs();
        String projectsFile = "src/main/java/com/yourteam/taiga/project_outputs/" + date + ".json";
        java.io.FileWriter writer = new java.io.FileWriter(projectsFile);
        writer.write(projectsJson);
        writer.close();
        System.out.println("Saved to " + projectsFile);

        // Clean and enrich with stories, tasks, sprints, members
        client.cleanJsonOutput(projectsFile, token);
    }
}
