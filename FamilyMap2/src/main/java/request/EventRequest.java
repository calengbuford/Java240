package request;

import model.AuthToken;

public class EventRequest {
    private AuthToken authToken;

    /**
     * Set AuthToken
     * @param authToken
     */
    public EventRequest(AuthToken authToken) {
        this.authToken = authToken;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
