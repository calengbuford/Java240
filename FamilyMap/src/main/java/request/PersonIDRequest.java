package request;

import model.AuthToken;

public class PersonIDRequest {
    private AuthToken authToken;

    /**
     * Set AuthToken
     * @param authToken
     */
    public PersonIDRequest(AuthToken authToken) {
        this.authToken = authToken;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
