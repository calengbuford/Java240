package request_;

import model_.AuthToken;

public class PersonIDRequest {
    private AuthToken authToken;

    /**
     * Empty constructor
     */
    public PersonIDRequest() { }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
