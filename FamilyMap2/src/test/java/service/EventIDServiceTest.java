package service;

import dao.DataAccessException;
import dao.Database;
import dao.EventDao;
import model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.ClearRequest;
import request.EventIDRequest;
import request.PersonIDRequest;
import request.RegisterRequest;
import response.EventIDResponse;
import response.PersonIDResponse;
import response.RegisterResponse;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class EventIDServiceTest {
    Database db = new Database();;
    String authToken;
    String personID;
    Event event;

    @BeforeEach
    public void setUp() throws Exception {
        // Register a user
        RegisterRequest registerRequest = new RegisterRequest("user1", "pass1", "email1",
                "jimmy", "spoon", "m");
        RegisterService registerService = new RegisterService();
        RegisterResponse response = registerService.register(registerRequest);
        authToken = response.getAuthToken();
        personID = response.getPersonID();

        try {
            Connection conn = db.openConnection();
            EventDao eventDao = new EventDao(conn);

            // Get one event from the user
            event = eventDao.getPersonEvents("user1")[0];

            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
    }

    @AfterEach
    public void tearDown() {
        // Clear the database
        ClearRequest request = new ClearRequest();
        ClearService service = new ClearService();
        service.clear(request);
    }

    @Test
    public void eventIDPass() {
        EventIDService service = new EventIDService();
        EventIDRequest request = new EventIDRequest();

        EventIDResponse response = service.event(request, authToken, event.getEventID());

        assertNotNull(response);

        assertEquals("user1", response.getAssociatedUsername());
        assertEquals(event.getEventID(), response.getEventID());
        assertEquals(personID, response.getPersonID());
        assertEquals(event.getLatitude(), response.getLatitude());
        assertEquals(event.getLongitude(), response.getLongitude());
        assertEquals(event.getCountry(), response.getCountry());
        assertEquals(event.getCity(), response.getCity());
        assertEquals(event.getEventType(), response.getEventType());
        assertEquals(event.getYear(), response.getYear());
        assertEquals(true, response.getSuccess());
        assertNull(response.getMessage());
    }

    @Test
    public void invalidAuthTokenFail() {
        EventIDService service = new EventIDService();
        EventIDRequest request = new EventIDRequest();

        EventIDResponse response = service.event(request, "otherAuthToken", event.getEventID());

        assertNotNull(response);

        assertEquals(false, response.getSuccess());
        assertEquals( "Error: AuthToken not valid" , response.getMessage());
        assertNull(response.getAssociatedUsername());
        assertNull(response.getEventID());
        assertNull(response.getPersonID());
        assertNull(response.getCountry());
        assertNull(response.getCity());
        assertNull(response.getEventType());
    }

    @Test
    public void invalidPersonIDFail() {
        EventIDService service = new EventIDService();
        EventIDRequest request = new EventIDRequest();

        EventIDResponse response = service.event(request, authToken, "otherEventID");

        assertNotNull(response);

        assertEquals(false, response.getSuccess());
        assertEquals( "Error: EventID not valid" , response.getMessage());
        assertNull(response.getAssociatedUsername());
        assertNull(response.getEventID());
        assertNull(response.getPersonID());
        assertNull(response.getCountry());
        assertNull(response.getCity());
        assertNull(response.getEventType());
    }

    @Test
    public void personNotOfUserFail() throws Exception {
        Event otherEvent = new Event();

        // Register another user
        RegisterRequest registerRequest = new RegisterRequest("user2", "pass2", "email2",
                "johnny", "appleseed", "m");
        RegisterService registerService = new RegisterService();
        RegisterResponse registerResponse = registerService.register(registerRequest);
        String otherPersonID = registerResponse.getPersonID();

        try {
            Connection conn = db.openConnection();
            EventDao eventDao = new EventDao(conn);

            // Get one event from the user
            otherEvent = eventDao.getPersonEvents("user2")[0];

            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }

        // Make service call
        EventIDService service = new EventIDService();
        EventIDRequest request = new EventIDRequest();

        EventIDResponse response = service.event(request, authToken, otherEvent.getEventID());

        assertNotNull(response);

        assertEquals(false, response.getSuccess());
        assertEquals( "Error: Requested event does not belong to this user" , response.getMessage());
        assertNull(response.getAssociatedUsername());
        assertNull(response.getEventID());
        assertNull(response.getPersonID());
        assertNull(response.getCountry());
        assertNull(response.getCity());
        assertNull(response.getEventType());
    }

}
