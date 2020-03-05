package service;

import dao_.*;
import model_.Event;
import model_.Person;
import model_.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request_.ClearRequest;
import request_.LoadRequest;
import request_.RegisterRequest;
import response_.LoadResponse;
import service_.ClearService;
import service_.LoadService;
import service_.RegisterService;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class LoadServiceTest {
    private Database db;
    private User[] users;
    private Event[] events;
    private Person[] persons;

    @BeforeEach
    public void setUp() {
        db = new Database();

        User user = new User("user1", "pass1", "email1", "johnny",
                "appleseed", "m", "!");
        Event event = new Event("111", "user1", "111", 1,
                1, "country1", "city1", "event1", 2020);
        Person person = new Person("1", "user1", "johnny", "appleseed", "m");

        users = new User[1];
        events = new Event[1];
        persons = new Person[1];

        users[0] = user;
        events[0] = event;
        persons[0] = person;
    }

    @AfterEach
    public void tearDown() {
        // Clear the database
        ClearRequest request = new ClearRequest();
        ClearService service = new ClearService();
        service.clear(request);
    }

    @Test
    public void loadPass() throws Exception {
        // Test a successful load

        // Register a user to be cleared by load
        RegisterRequest registerRequest = new RegisterRequest("user2", "pass2", "email2",
                "jimmy", "spoon", "m");
        RegisterService registerService = new RegisterService();
        registerService.register(registerRequest);

        // Build request
        LoadResponse response;
        LoadService service = new LoadService();
        LoadRequest request = new LoadRequest();
        request.setUsers(users);
        request.setEvents(events);
        request.setPersons(persons);

        response = service.load(request);

        assertNotNull(response);

        // Check contents of database
        try {
            Connection conn = db.openConnection();
            UserDao userDao = new UserDao(conn);
            EventDao eventDao = new EventDao(conn);
            PersonDao personDao = new PersonDao(conn);

            // Confirm previously registered user has been cleared from the database
            User user = userDao.getUser("user2");
            assertNull(user);

            // Confirm user1 data is in database
            user = userDao.getUser("user1");
            assertNotNull(user);

            events = eventDao.getPersonEvents("user1");
            assertNotNull(events);

            persons = personDao.getPersonFamily("user1");
            assertNotNull(persons);

            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }

        assertNotNull(response.getMessage());
        assertEquals("Successfully added 1 users, 1 persons, and 1 events to the database.", response.getMessage());
        assertEquals(true, response.getSuccess());
    }

    @Test
    public void missingValuesFail() {
        // Test loading with missing values in request

        // Build request, without setting users
        LoadResponse response;
        LoadService service = new LoadService();
        LoadRequest request = new LoadRequest();
        request.setEvents(events);
        request.setPersons(persons);

        response = service.load(request);

        assertNotNull(response);
        assertNotNull(response.getMessage());
        assertEquals("Error: Missing values", response.getMessage());
        assertEquals(false, response.getSuccess());
    }

}
