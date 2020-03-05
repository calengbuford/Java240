package service;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.UserDao;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.*;
import response.EventResponse;
import response.FillResponse;
import response.PersonResponse;

import java.security.spec.ECField;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class FillServiceTest {
    private User user;
    private String authToken;

    @BeforeEach
    public void setUp() throws Exception {
        Database db = new Database();

        // Register a user to be logged in
        RegisterRequest request = new RegisterRequest("user", "pass", "email",
                "jimmy", "spoon", "m");
        RegisterService service = new RegisterService();
        service.register(request);
        user = new User();
        user.setUserName("user");
        user.setPassword("pass");

        // Check that new user was created
        try {
            Connection conn = db.openConnection();
            AuthTokenDao authTokenDao = new AuthTokenDao(conn);

            authToken = authTokenDao.getAuthTokenByUserName(user.getUserName()).get(0).getToken();
            assertNotNull(authToken);

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
    public void fillZeroGenPass() throws Exception {
        FillResponse response;
        FillService service = new FillService();
        FillRequest request = new FillRequest();

        response = service.fill(request, user.getUserName(), "0");

        assertNotNull(response);

        assertTrue(response.getSuccess());
        assertEquals("Successfully added 1 persons and 1 events to the database.", response.getMessage());

        // Check that 0 generations were created
        EventService eventService = new EventService();
        EventRequest eventRequest = new EventRequest();
        EventResponse eventResponse = eventService.event(eventRequest, authToken);
        assertNotNull(eventResponse);

        assertEquals(1, eventResponse.getData().length);

        PersonService personService = new PersonService();
        PersonRequest personRequest = new PersonRequest();
        PersonResponse personResponse = personService.person(personRequest, authToken);
        assertNotNull(response);

        assertEquals(1, personResponse.getData().length);
    }

    @Test
    public void fillOneGenPass() throws Exception {
        FillResponse response;
        FillService service = new FillService();
        FillRequest request = new FillRequest();

        response = service.fill(request, user.getUserName(), "1");

        assertNotNull(response);

        assertTrue(response.getSuccess());
        assertEquals("Successfully added 3 persons and 7 events to the database.", response.getMessage());

        // Check that 1 generation was created
        EventService eventService = new EventService();
        EventRequest eventRequest = new EventRequest();
        EventResponse eventResponse = eventService.event(eventRequest, authToken);
        assertNotNull(eventResponse);

        assertEquals(7, eventResponse.getData().length);

        PersonService personService = new PersonService();
        PersonRequest personRequest = new PersonRequest();
        PersonResponse personResponse = personService.person(personRequest, authToken);
        assertNotNull(response);

        assertEquals(3, personResponse.getData().length);
    }

    @Test
    public void fillTwoGenPass() throws Exception {
        FillResponse response;
        FillService service = new FillService();
        FillRequest request = new FillRequest();

        response = service.fill(request, user.getUserName(), "2");

        assertNotNull(response);

        assertTrue(response.getSuccess());
        assertEquals("Successfully added 7 persons and 19 events to the database.", response.getMessage());

        // Check that 2 generations were created
        EventService eventService = new EventService();
        EventRequest eventRequest = new EventRequest();
        EventResponse eventResponse = eventService.event(eventRequest, authToken);
        assertNotNull(eventResponse);

        assertEquals(19, eventResponse.getData().length);

        PersonService personService = new PersonService();
        PersonRequest personRequest = new PersonRequest();
        PersonResponse personResponse = personService.person(personRequest, authToken);
        assertNotNull(response);

        assertEquals(7, personResponse.getData().length);
    }

    @Test
    public void fillThreeGenPass() throws Exception {
        FillResponse response;
        FillService service = new FillService();
        FillRequest request = new FillRequest();

        response = service.fill(request, user.getUserName(), "3");

        assertNotNull(response);

        assertTrue(response.getSuccess());
        assertEquals("Successfully added 15 persons and 43 events to the database.", response.getMessage());

        // Check that 3 generations were created
        EventService eventService = new EventService();
        EventRequest eventRequest = new EventRequest();
        EventResponse eventResponse = eventService.event(eventRequest, authToken);
        assertNotNull(eventResponse);

        assertEquals(43, eventResponse.getData().length);

        PersonService personService = new PersonService();
        PersonRequest personRequest = new PersonRequest();
        PersonResponse personResponse = personService.person(personRequest, authToken);
        assertNotNull(response);

        assertEquals(15, personResponse.getData().length);
    }

    @Test
    public void fillFourGenPass() throws Exception {
        FillResponse response;
        FillService service = new FillService();
        FillRequest request = new FillRequest();

        response = service.fill(request, user.getUserName(), "4");

        assertNotNull(response);

        assertTrue(response.getSuccess());
        assertEquals("Successfully added 31 persons and 91 events to the database.", response.getMessage());

        // Check that 4 generations were created
        EventService eventService = new EventService();
        EventRequest eventRequest = new EventRequest();
        EventResponse eventResponse = eventService.event(eventRequest, authToken);
        assertNotNull(eventResponse);

        assertEquals(91, eventResponse.getData().length);

        PersonService personService = new PersonService();
        PersonRequest personRequest = new PersonRequest();
        PersonResponse personResponse = personService.person(personRequest, authToken);
        assertNotNull(response);

        assertEquals(31, personResponse.getData().length);
    }

    @Test
    public void invalidUserNameFail() throws Exception {
        // Check current event and person data
        EventService eventService = new EventService();
        EventRequest eventRequest = new EventRequest();
        EventResponse eventResponse = eventService.event(eventRequest, authToken);
        assertNotNull(eventResponse);
        int numEvents = eventResponse.getData().length;

        PersonService personService = new PersonService();
        PersonRequest personRequest = new PersonRequest();
        PersonResponse personResponse = personService.person(personRequest, authToken);
        assertNotNull(personResponse);
        int numPersons = personResponse.getData().length;

        // Fill with a userName not registered
        FillResponse response;
        FillService service = new FillService();
        FillRequest request = new FillRequest();

        response = service.fill(request, "otherUser", "4");

        assertNotNull(response);

        assertFalse(response.getSuccess());
        assertEquals("Error: Invalid userName", response.getMessage());

        // Check that no events or persons were added to the database
        eventService = new EventService();
        eventRequest = new EventRequest();
        eventResponse = eventService.event(eventRequest, authToken);
        assertNotNull(eventResponse);

        assertEquals(numEvents, eventResponse.getData().length);

        personService = new PersonService();
        personRequest = new PersonRequest();
        personResponse = personService.person(personRequest, authToken);
        assertNotNull(personResponse);

        assertEquals(numPersons, personResponse.getData().length);
    }

    @Test
    public void invalidGenerationsParamFail1() throws Exception {
        // Check current event and person data
        EventService eventService = new EventService();
        EventRequest eventRequest = new EventRequest();
        EventResponse eventResponse = eventService.event(eventRequest, authToken);
        assertNotNull(eventResponse);
        int numEvents = eventResponse.getData().length;

        PersonService personService = new PersonService();
        PersonRequest personRequest = new PersonRequest();
        PersonResponse personResponse = personService.person(personRequest, authToken);
        assertNotNull(personResponse);
        int numPersons = personResponse.getData().length;

        // Fill with a userName not registered
        FillResponse response;
        FillService service = new FillService();
        FillRequest request = new FillRequest();

        response = service.fill(request, user.getUserName(), "-1");

        assertNotNull(response);

        assertFalse(response.getSuccess());
        assertEquals("Error: Invalid generations parameter", response.getMessage());

        // Check that no events or persons were added to the database
        eventService = new EventService();
        eventRequest = new EventRequest();
        eventResponse = eventService.event(eventRequest, authToken);
        assertNotNull(eventResponse);

        assertEquals(numEvents, eventResponse.getData().length);

        personService = new PersonService();
        personRequest = new PersonRequest();
        personResponse = personService.person(personRequest, authToken);
        assertNotNull(personResponse);

        assertEquals(numPersons, personResponse.getData().length);
    }

    @Test
    public void invalidGenerationsParamFail2() throws Exception {
        // Check current event and person data
        EventService eventService = new EventService();
        EventRequest eventRequest = new EventRequest();
        EventResponse eventResponse = eventService.event(eventRequest, authToken);
        assertNotNull(eventResponse);
        int numEvents = eventResponse.getData().length;

        PersonService personService = new PersonService();
        PersonRequest personRequest = new PersonRequest();
        PersonResponse personResponse = personService.person(personRequest, authToken);
        assertNotNull(personResponse);
        int numPersons = personResponse.getData().length;

        // Fill with a userName not registered
        FillResponse response;
        FillService service = new FillService();
        FillRequest request = new FillRequest();

        response = service.fill(request, user.getUserName(), "badGen");

        assertNotNull(response);

        assertFalse(response.getSuccess());
        assertEquals("Error: Invalid generations parameter", response.getMessage());

        // Check that no events or persons were added to the database
        eventService = new EventService();
        eventRequest = new EventRequest();
        eventResponse = eventService.event(eventRequest, authToken);
        assertNotNull(eventResponse);

        assertEquals(numEvents, eventResponse.getData().length);

        personService = new PersonService();
        personRequest = new PersonRequest();
        personResponse = personService.person(personRequest, authToken);
        assertNotNull(personResponse);

        assertEquals(numPersons, personResponse.getData().length);
    }

}
