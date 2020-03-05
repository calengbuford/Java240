package service;

import dao.DataAccessException;
import dao.Database;
import dao.UserDao;
import model.Event;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import request.ClearRequest;
import request.EventRequest;
import request.PersonRequest;
import request.RegisterRequest;
import response.EventResponse;
import response.PersonResponse;
import response.RegisterResponse;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTest {
    private Database db = new Database();
    private Event bestEvent;

    @AfterEach
    public void tearDown() {
        // Clear the database
        ClearRequest request = new ClearRequest();
        ClearService service = new ClearService();
        service.clear(request);
    }

    @Test
    public void registerPass() throws Exception {
        // Register a user
        RegisterRequest registerRequest = new RegisterRequest("user1", "pass1", "email1",
                "jimmy", "spoon", "m");
        RegisterService registerService = new RegisterService();

        RegisterResponse response = registerService.register(registerRequest);
        assertNotNull(response);

        // Check that authToken was returned and other response values
        assertNotNull(response.getAuthToken());
        assertEquals("user1", response.getUserName());
        assertNotNull(response.getPersonID());
        assertTrue(response.getSuccess());

        // Check that new user was created
        try {
            Connection conn = db.openConnection();
            UserDao userDao = new UserDao(conn);

            User newUser = userDao.getUser(response.getUserName());
            assertNotNull(newUser);

            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }

        // Check that 4 generations were created
        EventService eventService = new EventService();
        EventRequest eventRequest = new EventRequest();
        EventResponse eventResponse = eventService.event(eventRequest, response.getAuthToken());
        assertNotNull(eventResponse);

        assertEquals(91, eventResponse.getData().length);

        PersonService personService = new PersonService();
        PersonRequest personRequest = new PersonRequest();
        PersonResponse personResponse = personService.person(personRequest, response.getAuthToken());
        assertNotNull(personResponse);

        assertEquals(31, personResponse.getData().length);
    }

    @Test
    public void missingValueFail() {
        // Register a user with missing values
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUserName("user1");
        RegisterService registerService = new RegisterService();

        RegisterResponse response = registerService.register(registerRequest);
        assertNotNull(response);

        // Check that error response is returned
        assertFalse(response.getSuccess());
        assertEquals("Error: Invalid request value", response.getMessage());
        assertNull(response.getAuthToken());
        assertNull(response.getUserName());
        assertNull(response.getPersonID());
    }

    @Test
    public void invalidValueEmptyStringFail() {
        // Register a user with missing values
        RegisterRequest registerRequest = new RegisterRequest("", "pass1", "email1",
                "jimmy", "spoon", "f");
        RegisterService registerService = new RegisterService();

        RegisterResponse response = registerService.register(registerRequest);
        assertNotNull(response);

        // Check that error response is returned
        assertFalse(response.getSuccess());
        assertEquals("Error: Invalid request value", response.getMessage());
        assertNull(response.getAuthToken());
        assertNull(response.getUserName());
        assertNull(response.getPersonID());
    }

    @Test
    public void invalidValueGenderFail() {
        // Register a user with missing values
        RegisterRequest registerRequest = new RegisterRequest("user1", "pass1", "email1",
                "jimmy", "spoon", "badGender");
        RegisterService registerService = new RegisterService();

        RegisterResponse response = registerService.register(registerRequest);
        assertNotNull(response);

        // Check that error response is returned
        assertFalse(response.getSuccess());
        assertEquals("Error: Invalid request value", response.getMessage());
        assertNull(response.getAuthToken());
        assertNull(response.getUserName());
        assertNull(response.getPersonID());
    }

    @Test
    public void userNameAlreadyTaken() {
        // Register a user
        RegisterRequest registerRequest1 = new RegisterRequest("user1", "pass1", "email1",
                "jimmy", "spoon", "m");
        RegisterService registerService1 = new RegisterService();

        RegisterResponse response1 = registerService1.register(registerRequest1);
        assertNotNull(response1);

        // Register another user with the same userName
        RegisterRequest registerRequest2 = new RegisterRequest("user1", "pass2", "email2",
                "jimmy", "spoon", "m");
        RegisterService registerService2 = new RegisterService();

        RegisterResponse response2 = registerService2.register(registerRequest2);
        assertNotNull(response2);

        // Check that error response is returned
        assertFalse(response2.getSuccess());
        assertEquals("Error: User already registered in the database", response2.getMessage());
        assertNull(response2.getAuthToken());
        assertNull(response2.getUserName());
        assertNull(response2.getPersonID());
    }

}

