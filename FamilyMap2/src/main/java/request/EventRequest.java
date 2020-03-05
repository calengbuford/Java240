package request;

import model.AuthToken;

public class EventRequest {
    private AuthToken authToken;

    /**
     * Empty constructor
     */
    public EventRequest() { }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
