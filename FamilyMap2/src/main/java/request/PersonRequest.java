package request;

import model.AuthToken;

public class PersonRequest {
    private AuthToken authToken;

    /**
     * Set AuthToken
     * @param authToken
     */
    public PersonRequest(AuthToken authToken) {
        this.authToken = authToken;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
