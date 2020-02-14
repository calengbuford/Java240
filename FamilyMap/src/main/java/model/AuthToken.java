package model;

import java.util.UUID;

public class AuthToken {
    private String userName = null;
    private String password = null;
    private String authToken = null;

    /**
     * Create an AuthToken with a userName, a password, and a random authToken string
     * @param userName the userName associated with the AuthToken
     * @param password the password associated with the AuthToken
     */
    public AuthToken(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.authToken = UUID.randomUUID().toString();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
