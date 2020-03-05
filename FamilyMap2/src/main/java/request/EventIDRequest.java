package request;

import model.AuthToken;

public class EventIDRequest {
    private AuthToken authToken;

    /**
     * Empty constructor
     */
    public EventIDRequest() { }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
