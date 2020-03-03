package dao;

import model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class PersonDaoTest {
    private Database db;
    private Person bestPerson;

    @BeforeEach
    public void setUp() throws Exception {
        // Set up any classes or variables we will need for the rest of our tests
        db = new Database();    // Create a new database

        // Create a new person with random data
        bestPerson = new Person("1111", "Gage", "Glen",
                "Smith", "Male", "2222", "3333", "4444");
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
    public void createPersonPass() throws Exception {
        // Make sure createPerson works
        // First create a Person set to null. Use this to make sure what we put
        // in the database is actually there.
        Person compareTest = null;

        try {
            // Get our connection and make a new Dao
            Connection conn = db.openConnection();
            PersonDao personDao = new PersonDao(conn);
            // While createPerson returns a bool we can't use that to verify that our function actually worked
            // Only that it ran without causing an error
            personDao.createPerson(bestPerson);
            // So use a getPerson method to get the user that we just put in back out
            compareTest = personDao.getPerson(bestPerson.getPersonID());
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
        assertEquals(bestPerson, compareTest);
    }

    @Test
    public void createPersonFail() throws Exception {
        // Test again but try to make it fail

        boolean didItWork = true;
        try {
            Connection conn = db.openConnection();
            PersonDao personDao = new PersonDao(conn);
            // if we call the method the first time it will createPerson successfully
            personDao.createPerson(bestPerson);
            // but our sql table is set up so that "personID" must be unique. So trying to insert it
            // again will cause the method to throw an exception
            personDao.createPerson(bestPerson);
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
        // to make sure that our person is not in the database
        // Set our compareTest to an actual person
        Person compareTest = bestPerson;
        try {
            Connection conn = db.openConnection();
            PersonDao personDao = new PersonDao(conn);
            // Then get something back from our getPerson. If the person is not in the database we
            // should have just changed our compareTest to a null object
            compareTest = personDao.getPerson(bestPerson.getPersonID());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }

        // Now make sure that compareTest is indeed null
        assertNull(compareTest);
    }

    @Test
    public void getPersonPass() throws Exception {
        // Make sure getPerson works
        // First create a Person set to null. Use this to make sure what we put
        // in the database is actually there.
        Person compareTest = null;

        try {
            // Get our connection and make a new Dao
            Connection conn = db.openConnection();
            PersonDao personDao = new PersonDao(conn);
            personDao.createPerson(bestPerson);

            compareTest = personDao.getPerson(bestPerson.getPersonID());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        // First see if our find found anything at all. If it did then we know that if nothing
        // else something was put into our database, since we cleared it in the beginning
        assertNotNull(compareTest);
        // Now lets make sure that what we put in is exactly the same as what we got out. If this
        // passes then we know that our getPerson did put something in, and that it didn't change the
        // data in any way
        assertEquals(bestPerson, compareTest);
    }

    @Test
    public void getPersonFail() throws Exception {
        // Test again but try to make it fail

        Person person = null;
        try {
            Connection conn = db.openConnection();
            PersonDao personDao = new PersonDao(conn);

            // Attempt to retrieve a person that does not exist, since the table is empty
            person = personDao.getPerson("1234");

            db.closeConnection(true);
        } catch (DataAccessException e) {
            // If we catch an exception we will end up in here, where we can change our boolean to
            // false to show that our function failed to perform correctly
            db.closeConnection(false);
        }
        // Ensure no person was returned
        assertNull(person);
    }

    @Test
    public void deletePersonTablePass() throws Exception {
        // First create a Person set to null. Use this to make sure what we put
        // in the database is actually there.
        Person compareTest = null;

        try {
            // Get our connection and make a new Dao
            Connection conn = db.openConnection();
            PersonDao personDao = new PersonDao(conn);
            personDao.createPerson(bestPerson);

            // Delete all rows of the table, then try to get a person from the table
            personDao.deleteAllPersons();
            compareTest = personDao.getPerson(bestPerson.getAssociatedUsername());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        // Check to see if compareTest is null. This means the database has been cleared
        assertNull(compareTest);
    }

}
