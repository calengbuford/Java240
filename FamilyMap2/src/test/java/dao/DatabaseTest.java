package dao;

import model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {
    private Database db;
    private Event bestEvent;

    @BeforeEach
    public void setUp() throws Exception {
        //here we can set up any classes or variables we will need for the rest of our tests
        //lets create a new database
        db = new Database();
        //and a new event with random data
        bestEvent = new Event("Biking_123A", "Gale", "Gale123A",
                10.3f, 10.3f, "Japan", "Ushiku",
                "Biking_Around", 2016);
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
    public void commitChange() throws Exception {
        // Check that we can commit something to the database
        Event compareTest = null;
        try {
            Connection conn = db.openConnection();
            EventDao eventDao = new EventDao(conn);

            eventDao.createEvent(bestEvent);

            db.closeConnection(true);

            // Now check that the event was committed to the database
            conn = db.openConnection();
            eventDao = new EventDao(conn);

            compareTest = eventDao.getEvent(bestEvent.getEventID());

            db.closeConnection(true);

        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        // compareTest should not be null if something was actually added
        assertNotNull(compareTest);
    }

    @Test
    public void rollBackChange() throws Exception {
        // Check that we can commit something to the database
        Event compareTest = null;
        try {
            Connection conn = db.openConnection();
            EventDao eventDao = new EventDao(conn);

            eventDao.createEvent(bestEvent);

            db.closeConnection(false);

            // Now check that the event was not committed to the database
            conn = db.openConnection();
            eventDao = new EventDao(conn);

            compareTest = eventDao.getEvent(bestEvent.getEventID());

            db.closeConnection(true);

        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        // compareTest should be null if the database was rolled back
        assertNull(compareTest);
    }

}
