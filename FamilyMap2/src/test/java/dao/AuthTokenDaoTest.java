package dao;

import model.AuthToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

//We will use this to test that our insert method is working and failing in the right ways
public class AuthTokenDaoTest {
    private Database db;
    private AuthToken bestAuthToken;

    @BeforeEach
    public void setUp() throws Exception {
        // Set up any classes or variables we will need for the rest of our tests
        db = new Database();    // Create a new database
        // Create a new authToken with random data
        bestAuthToken = new AuthToken("5555", "Snow", "1234");
    }

    @AfterEach
    public void tearDown() throws Exception {
        //here we can get rid of anything from our tests we don't want to affect the rest of our program
        //lets clear the tables so that any data we entered for testing doesn't linger in our files
        db.openConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void createAuthTokenPass() throws Exception {
        //We want to make sure insert works
        //First lets create an AuthToken that we'll set to null. We'll use this to make sure what we put
        //in the database is actually there.
        AuthToken compareTest = null;

        try {
            //Let's get our connection and make a new DAO
            Connection conn = db.openConnection();
            AuthTokenDao authTokenDao = new AuthTokenDao(conn);
            //While insert returns a bool we can't use that to verify that our function actually worked
            //only that it ran without causing an error
            authTokenDao.createAuthToken(bestAuthToken);
            //So lets use a find method to get the authToken that we just put in back out
            compareTest = authTokenDao.getAuthTokenByToken(bestAuthToken.getToken());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        //First lets see if our find found anything at all. If it did then we know that if nothing
        //else something was put into our database, since we cleared it in the beginning
        assertNotNull(compareTest);
        //Now lets make sure that what we put in is exactly the same as what we got out. If this
        //passes then we know that our insert did put something in, and that it didn't change the
        //data in any way
        assertEquals(bestAuthToken, compareTest);

    }

    @Test
    public void createAuthTokenFail() throws Exception {
        //lets do this test again but this time lets try to make it fail

        boolean didItWork = true;
        try {
            Connection conn = db.openConnection();
            AuthTokenDao authTokenDao = new AuthTokenDao(conn);
            //if we call the method the first time it will insert it successfully
            authTokenDao.createAuthToken(bestAuthToken);
            //but our sql table is set up so that "token" must be unique. So trying to insert it
            //again will cause the method to throw an exception
            authTokenDao.createAuthToken(bestAuthToken);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            //If we catch an exception we will end up in here, where we can change our boolean to
            //false to show that our function failed to perform correctly
            db.closeConnection(false);
            didItWork = false;
        }
        //Check to make sure that we did in fact enter our catch statement
        assertFalse(didItWork);

        //Since we know our database encountered an error, both instances of insert should have been
        //rolled back. So for added security lets make one more quick check using our find function
        //to make sure that our event is not in the database
        //Set our compareTest to an actual event
        AuthToken compareTest = bestAuthToken;
        try {
            Connection conn = db.openConnection();
            AuthTokenDao authTokenDao = new AuthTokenDao(conn);
            //and then get something back from our find. If the authToken is not in the database we
            //should have just changed our compareTest to a null object
            compareTest = authTokenDao.getAuthTokenByToken(bestAuthToken.getToken());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }

        //Now make sure that compareTest is indeed null
        assertNull(compareTest);
    }

    @Test
    public void getAuthTokenPass() throws Exception {
        // Make sure getAuthToken works
        // First create a AuthToken set to null. Use this to make sure what we put
        // in the database is actually there.
        AuthToken compareTest = null;

        try {
            // Get our connection and make a new Dao
            Connection conn = db.openConnection();
            AuthTokenDao authTokenDao = new AuthTokenDao(conn);
            authTokenDao.createAuthToken(bestAuthToken);

            compareTest = authTokenDao.getAuthTokenByToken(bestAuthToken.getToken());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        // First see if our find found anything at all. If it did then we know that if nothing
        // else something was put into our database, since we cleared it in the beginning
        assertNotNull(compareTest);
        // Now lets make sure that what we put in is exactly the same as what we got out. If this
        // passes then we know that our getAuthToken did put something in, and that it didn't change the
        // data in any way
        assertEquals(bestAuthToken, compareTest);
    }

    @Test
    public void getAuthTokenFail() throws Exception {
        // Test again but try to make it fail

        AuthToken authToken = null;
        try {
            Connection conn = db.openConnection();
            AuthTokenDao authTokenDao = new AuthTokenDao(conn);

            // Attempt to retrieve an authToken that does not exist, since the table is empty
            authToken = authTokenDao.getAuthTokenByToken("1234");

            db.closeConnection(true);
        } catch (DataAccessException e) {
            // If we catch an exception we will end up in here, where we can change our boolean to
            // false to show that our function failed to perform correctly
            db.closeConnection(false);
        }
        // Ensure no authToken was returned
        assertNull(authToken);
    }

    @Test
    public void deleteAuthTokenTablePass() throws Exception {
        // First create an AuthToken set to null. Use this to make sure what we put
        // in the database is actually there.
        AuthToken compareTest = null;

        try {
            // Get our connection and make a new Dao
            Connection conn = db.openConnection();
            AuthTokenDao authTokenDao = new AuthTokenDao(conn);
            authTokenDao.createAuthToken(bestAuthToken);

            // Delete all rows of the table, then try to get an authToken from the table
            authTokenDao.deleteAllAuthTokens();
            compareTest = authTokenDao.getAuthTokenByToken(bestAuthToken.getToken());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        // Check to see if compareTest is null. This means the database has been cleared
        assertNull(compareTest);
    }

}
