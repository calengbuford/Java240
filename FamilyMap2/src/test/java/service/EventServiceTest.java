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
import request.EventRequest;
import request.RegisterRequest;
import response.EventIDResponse;
import response.EventResponse;
import response.RegisterResponse;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class EventServiceTest {
    Database db = new Database();
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
    public void eventPass() {
        EventService service = new EventService();
        EventRequest request = new EventRequest();

        EventResponse response = service.event(request, authToken);

        assertNotNull(response);

        assertEquals("user1", response.getData()[0].getAssociatedUsername());
        assertEquals(event.getEventID(), response.getData()[0].getEventID());
        assertEquals(personID, response.getData()[0].getPersonID());
        assertEquals(event.getLatitude(), response.getData()[0].getLatitude());
        assertEquals(event.getLongitude(), response.getData()[0].getLongitude());
        assertEquals(event.getCountry(), response.getData()[0].getCountry());
        assertEquals(event.getCity(), response.getData()[0].getCity());
        assertEquals(event.getEventType(), response.getData()[0].getEventType());
        assertEquals(event.getYear(), response.getData()[0].getYear());
        assertEquals(true, response.getSuccess());
        assertNull(response.getMessage());
    }

    @Test
    public void invalidAuthTokenFail() {
        EventService service = new EventService();
        EventRequest request = new EventRequest();

        EventResponse response = service.event(request, "otherAuthToken");

        assertNotNull(response);

        assertEquals(false, response.getSuccess());
        assertEquals( "Error: AuthToken not valid" , response.getMessage());
        assertNull(response.getData());
    }

}
