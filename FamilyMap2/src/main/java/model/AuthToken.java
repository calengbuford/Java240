package model;

import java.util.UUID;

public class AuthToken {
    private String token = null;
    private String userName = null;
    private String password = null;

    /**
     * Create an AuthToken with a userName, a password, and a random token string
     * @param userName the userName associated with the AuthToken
     * @param password the password associated with the AuthToken
     */
    public AuthToken(String userName, String password) {
        this.token = UUID.randomUUID().toString();
        this.userName = userName;
        this.password = password;
    }

    /**
     * Create an AuthToken with a userName, a password, and a random token string
     * @param token the token associated with the AuthToken
     * @param userName the userName associated with the AuthToken
     * @param password the password associated with the AuthToken
     */
    public AuthToken(String token, String userName, String password) {
        this.token = token;
        this.userName = userName;
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    /**
     * Check equality
     * @param o Object to compare
     * @return if o is equal to this authToken
     */
    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof AuthToken) {
            AuthToken oAuthToken = (AuthToken) o;
            return oAuthToken.getUserName().equals(getUserName()) &&
                    oAuthToken.getPassword().equals(getPassword()) &&
                    oAuthToken.getToken().equals(getToken());
        } else {
            return false;
        }
    }
}
