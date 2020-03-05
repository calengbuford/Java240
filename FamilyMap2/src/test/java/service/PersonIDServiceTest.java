package service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.ClearRequest;
import request.PersonIDRequest;
import request.RegisterRequest;
import response.PersonIDResponse;
import response.RegisterResponse;

import static org.junit.jupiter.api.Assertions.*;

public class PersonIDServiceTest {
    String authToken;
    String personID;

    @BeforeEach
    public void setUp() {
        // Register a user
        RegisterRequest registerRequest = new RegisterRequest("user1", "pass1", "email1",
                "jimmy", "spoon", "m");
        RegisterService registerService = new RegisterService();
        RegisterResponse response = registerService.register(registerRequest);
        authToken = response.getAuthToken();
        personID = response.getPersonID();
    }

    @AfterEach
    public void tearDown() {
        // Clear the database
        ClearRequest request = new ClearRequest();
        ClearService service = new ClearService();
        service.clear(request);
    }

    @Test
    public void personIDPass() {
        PersonIDService service = new PersonIDService();
        PersonIDRequest request = new PersonIDRequest();

        PersonIDResponse response = service.person(request, authToken, personID);

        assertNotNull(response);

        assertEquals("user1", response.getAssociatedUsername());
        assertEquals(personID, response.getPersonID());
        assertEquals("jimmy", response.getFirstName());
        assertEquals("spoon", response.getLastName());
        assertEquals("m", response.getGender());
        assertNotNull(response.getFatherID());
        assertNotNull(response.getMotherID());
        assertNull(response.getSpouseID());
        assertEquals(true, response.getSuccess());
        assertNull(response.getMessage());
    }

    @Test
    public void invalidAuthTokenFail() {
        PersonIDService service = new PersonIDService();
        PersonIDRequest request = new PersonIDRequest();

        PersonIDResponse response = service.person(request, "otherAuthToken", personID);

        assertNotNull(response);

        assertEquals(false, response.getSuccess());
        assertEquals( "Error: AuthToken not valid" , response.getMessage());
        assertNull(response.getAssociatedUsername());
        assertNull(response.getPersonID());
        assertNull(response.getFirstName());
        assertNull(response.getLastName());
        assertNull(response.getGender());
        assertNull(response.getFatherID());
        assertNull(response.getMotherID());
        assertNull(response.getSpouseID());
    }

    @Test
    public void invalidPersonIDFail() {
        PersonIDService service = new PersonIDService();
        PersonIDRequest request = new PersonIDRequest();

        PersonIDResponse response = service.person(request, authToken, "otherPersonID");

        assertNotNull(response);

        assertEquals(false, response.getSuccess());
        assertEquals( "Error: PersonID not valid" , response.getMessage());
        assertNull(response.getAssociatedUsername());
        assertNull(response.getPersonID());
        assertNull(response.getFirstName());
        assertNull(response.getLastName());
        assertNull(response.getGender());
        assertNull(response.getFatherID());
        assertNull(response.getMotherID());
        assertNull(response.getSpouseID());
    }

    @Test
    public void personNotOfUserFail() {
        // Register another user
        RegisterRequest registerRequest = new RegisterRequest("user2", "pass2", "email2",
                "johnny", "appleseed", "m");
        RegisterService registerService = new RegisterService();
        RegisterResponse registerResponse = registerService.register(registerRequest);
        String otherPersonID = registerResponse.getPersonID();

        // Make service call
        PersonIDService service = new PersonIDService();
        PersonIDRequest request = new PersonIDRequest();

        PersonIDResponse response = service.person(request, authToken, otherPersonID);

        assertNotNull(response);

        assertEquals(false, response.getSuccess());
        assertEquals( "Error: Requested person does not belong to this user" , response.getMessage());
        assertNull(response.getAssociatedUsername());
        assertNull(response.getPersonID());
        assertNull(response.getFirstName());
        assertNull(response.getLastName());
        assertNull(response.getGender());
        assertNull(response.getFatherID());
        assertNull(response.getMotherID());
        assertNull(response.getSpouseID());
    }

}
