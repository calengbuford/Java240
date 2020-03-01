package request;

import model.AuthToken;

public class EventIDRequest {
    private AuthToken authToken;

    /**
     * Set AuthToken
     * @param authToken
     */
    public EventIDRequest(AuthToken authToken) {
        this.authToken = authToken;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
