package com.yourteam.taiga;

/**
 * Holds the authentication token returned by Taiga after a successful login.
 * Pass this into any TaigaClient method that requires authentication.
 *
 * @author Ivan Torriani
 * @version 1.0
 */
public class TaigaAuthToken {

    private final String token;

    public TaigaAuthToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
