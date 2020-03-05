package service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request_.ClearRequest;
import request_.PersonRequest;
import request_.RegisterRequest;
import response_.PersonResponse;
import response_.RegisterResponse;
import service_.ClearService;
import service_.PersonService;
import service_.RegisterService;

import static org.junit.jupiter.api.Assertions.*;

public class PersonServiceTest {
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
    public void personPass() {
        PersonService service = new PersonService();
        PersonRequest request = new PersonRequest();

        PersonResponse response = service.person(request, authToken);

        assertNotNull(response);

        assertEquals("user1", response.getData()[0].getAssociatedUsername());
        assertEquals(personID, response.getData()[0].getPersonID());
        assertEquals("jimmy", response.getData()[0].getFirstName());
        assertEquals("spoon", response.getData()[0].getLastName());
        assertEquals("m", response.getData()[0].getGender());
        assertNotNull(response.getData()[0].getFatherID());
        assertNotNull(response.getData()[0].getMotherID());
        assertNull(response.getData()[0].getSpouseID());
        assertEquals(true, response.getSuccess());
        assertNull(response.getMessage());
    }

    @Test
    public void invalidAuthTokenFail() {
        PersonService service = new PersonService();
        PersonRequest request = new PersonRequest();

        PersonResponse response = service.person(request, "otherAuthToken");

        assertNotNull(response);

        assertEquals(false, response.getSuccess());
        assertEquals( "Error: AuthToken not valid" , response.getMessage());
        assertNull(response.getData());
    }

}
