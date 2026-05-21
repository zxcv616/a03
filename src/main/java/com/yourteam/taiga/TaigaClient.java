package com.yourteam.taiga;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import java.io.IOException;

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

        //api setup
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
        return get(baseUrl + "/api/v1/projects/" + projectId + "/members", token);
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

    //Hardcoding credentials at this point
    public static void main(String[] args) throws IOException {
        TaigaClient client = new TaigaClient("https://api.taiga.io");
        TaigaAuthToken token = client.authenticateUser("itorrian@calpoly.edu", "Ilikecamels1!");
        System.out.println("Token: " + token.getToken());

        String projectsJson = client.fetchProjects(token);
        System.out.println("Projects: " + projectsJson);

        // Write output json file
        java.io.File outputDir = new java.io.File("src/main/java/com/yourteam/taiga/project_outputs");
        outputDir.mkdirs();
        java.io.FileWriter writer = new java.io.FileWriter("src/main/java/com/yourteam/taiga/project_outputs/projects.json");
        writer.write(projectsJson);
        writer.close();
        System.out.println("Saved to src/main/java/com/yourteam/taiga/project_outputs/projects.json");
    }
}
