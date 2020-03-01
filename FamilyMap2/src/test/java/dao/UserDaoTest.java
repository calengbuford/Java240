package dao;

import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

//We will use this to test that our insert and retrieve methods are working and failing in the right ways
public class UserDaoTest {
    private Database db;
    private User bestUser;

    @BeforeEach
    public void setUp() throws Exception {
        // Set up any classes or variables we will need for the rest of our tests
        db = new Database();    // Create a new database

        // Create a new user with random data
        bestUser = new User("Gage", "1234", "this@that.com",
                "Glen", "Smith", "Male", "1111");
    }

    @AfterEach
    public void tearDown() throws Exception {
        // here we can get rid of anything from our tests we don't want to affect the rest of our program
        // lets clear the tables so that any data we entered for testing doesn't linger in our files
        db.openConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void createUserPass() throws Exception {
        // Make sure createUser works
        // First create a User set to null. Use this to make sure what we put
        // in the database is actually there.
        User compareTest = null;

        try {
            // Get our connection and make a new Dao
            Connection conn = db.openConnection();
            UserDao userDao = new UserDao(conn);
            // While createUser returns a bool we can't use that to verify that our function actually worked
            // Only that it ran without causing an error
            userDao.createUser(bestUser);
            // So use a getUser method to get the user that we just put in back out
            compareTest = userDao.getUser(bestUser.getUserName());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        // First see if our find found anything at all. If it did then we know that if nothing
        // else something was put into our database, since we cleared it in the beginning
        assertNotNull(compareTest);
        // Now lets make sure that what we put in is exactly the same as what we got out. If this
        // passes then we know that our createUser did put something in, and that it didn't change the
        // data in any way
        assertEquals(bestUser, compareTest);
    }

    @Test
    public void createUserFail() throws Exception {
        // Test again but try to make it fail

        boolean didItWork = true;
        try {
            Connection conn = db.openConnection();
            UserDao userDao = new UserDao(conn);
            // if we call the method the first time it will createUser successfully
            userDao.createUser(bestUser);
            // but our sql table is set up so that "userName" must be unique. So trying to insert it
            // again will cause the method to throw an exception
            userDao.createUser(bestUser);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            // If we catch an exception we will end up in here, where we can change our boolean to
            // false to show that our function failed to perform correctly
            db.closeConnection(false);
            didItWork = false;
        }
        // Check to make sure that we did in fact enter our catch statement
        assertFalse(didItWork);

        // Since we know our database encountered an error, both instances of insert should have been
        // rolled back. So for added security lets make one more quick check using our getUser function
        // to make sure that our user is not in the database
        // Set our compareTest to an actual user
        User compareTest = bestUser;
        try {
            Connection conn = db.openConnection();
            UserDao userDao = new UserDao(conn);
            // Then get something back from our getUser. If the user is not in the database we
            // should have just changed our compareTest to a null object
            compareTest = userDao.getUser(bestUser.getUserName());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }

        // Now make sure that compareTest is indeed null
        assertNull(compareTest);
    }

    @Test
    public void getUserPass() throws Exception {
        // Make sure getUser works
        // First create a User set to null. Use this to make sure what we put
        // in the database is actually there.
        User compareTest = null;

        try {
            // Get our connection and make a new Dao
            Connection conn = db.openConnection();
            UserDao userDao = new UserDao(conn);
            userDao.createUser(bestUser);

            compareTest = userDao.getUser(bestUser.getUserName());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        // First see if our find found anything at all. If it did then we know that if nothing
        // else something was put into our database, since we cleared it in the beginning
        assertNotNull(compareTest);
        // Now lets make sure that what we put in is exactly the same as what we got out. If this
        // passes then we know that our getUser did put something in, and that it didn't change the
        // data in any way
        assertEquals(bestUser, compareTest);
    }

    @Test
    public void getUserFail() throws Exception {
        // Test again but try to make it fail

        User user = null;
        try {
            Connection conn = db.openConnection();
            UserDao userDao = new UserDao(conn);

            // Attempt to retrieve a user that does not exist, since the table is empty
            user = userDao.getUser("1234");

            db.closeConnection(true);
        } catch (DataAccessException e) {
            // If we catch an exception we will end up in here, where we can change our boolean to
            // false to show that our function failed to perform correctly
            db.closeConnection(false);
        }
        // Ensure no user was returned
        assertNull(user);
    }

    @Test
    public void deleteUserTablePass() throws Exception {
        // First create a User set to null. Use this to make sure what we put
        // in the database is actually there.
        User compareTest = null;

        try {
            // Get our connection and make a new Dao
            Connection conn = db.openConnection();
            UserDao userDao = new UserDao(conn);
            userDao.createUser(bestUser);

            // Delete all rows of the table, then try to get a user from the table
            userDao.deleteAllUsers();
            compareTest = userDao.getUser(bestUser.getUserName());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        // Check to see if compareTest is null. This means the database has been cleared
        assertNull(compareTest);
    }

}
