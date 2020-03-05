package service;

import dao_.DataAccessException;
import dao_.Database;
import dao_.EventDao;
import model_.Event;
import model_.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request_.ClearRequest;
import request_.RegisterRequest;
import service_.ClearService;
import service_.RegisterService;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    private User testUser;
    private Database db;

    @BeforeEach
    public void setUp() {
        // Set up any classes or variables we will need for the rest of our tests
        db = new Database();    // Create a new database

        // Create a new user with random data
        testUser = new User("Gage", "1234", "this@that.com",
                "Glen", "Smith", "Male", "1111");
    }

    @Test
    public void clearEmptyDatabase() throws Exception {
        // Clear the database
        ClearRequest request = new ClearRequest();
        ClearService service = new ClearService();
        service.clear(request);

        try {
            Connection conn = db.openConnection();
            EventDao eventDao = new EventDao(conn);

            // Confirm database is still empty
            Event[] event = eventDao.getPersonEvents(testUser.getUserName());
            assertNull(event);

            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
    }

    @Test
    public void clearNonEmptyDatabase() throws Exception {
        // Register a user in the database
        RegisterRequest registerRequest = new RegisterRequest("user", "pass", "email",
                "jimmy", "spoon", "m");
        RegisterService registerService = new RegisterService();
        registerService.register(registerRequest);

        // Confirm the database is not empty
        try {
            Connection conn = db.openConnection();
            EventDao eventDao = new EventDao(conn);

            // Confirm database is still empty
            Event[] event = eventDao.getPersonEvents("user");
            assertNotNull(event);

            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }

        // Clear the database
        ClearRequest request = new ClearRequest();
        ClearService service = new ClearService();
        service.clear(request);

        // Confirm database is empty
        try {
            Connection conn = db.openConnection();
            EventDao eventDao = new EventDao(conn);

            Event[] event = eventDao.getPersonEvents(testUser.getUserName());
            assertNull(event);

            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
    }

}
