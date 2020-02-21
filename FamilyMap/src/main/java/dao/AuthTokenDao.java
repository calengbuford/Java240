package dao;
import model.AuthToken;

public class AuthTokenDao {

    /**
     * Empty constructor
     */
    public AuthTokenDao() { }

    /**
     * Create an authToken in the database
     * @param authToken the authToken to create
     */
    public void createAuthToken(AuthToken authToken) {

    }

    /**
     * Get an AuthToken from the database
     * @param userName the userName associated with the AuthToken
     * @param password the password associated with the AuthToken
     * @return the AuthToken created
     */
    public AuthToken getAuthToken(String userName, String password) {
        return null;
    }

    /**
     * Remove the authToken from the database
     * @param userName the name of the user from which to delete the authToken
     */
    public void deleteAuthToken(String userName) { }

    /**
     * Remove the all authTokens from the database
     */
    public void deleteAllAuthTokens() { }

    /**
     * Update the authToken's table information
     */
    public void updateAuthToken() { }
}
