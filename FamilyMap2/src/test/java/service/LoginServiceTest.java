package service;

import model_.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request_.ClearRequest;
import request_.LoginRequest;
import request_.RegisterRequest;
import response_.LoginResponse;
import service_.ClearService;
import service_.LoginService;
import service_.RegisterService;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {
    private User user;

    @BeforeEach
    public void setUp() {
        // Register a user to be logged in
        RegisterRequest request = new RegisterRequest("user", "pass", "email",
                "jimmy", "spoon", "m");
        RegisterService service = new RegisterService();
        service.register(request);
        user = new User();
        user.setUserName("user");
        user.setPassword("pass");
    }

    @AfterEach
    public void tearDown() {
        // Clear the database
        ClearRequest request = new ClearRequest();
        ClearService service = new ClearService();
        service.clear(request);
    }

    @Test
    public void loginPass() {
        // Test a successful login
        LoginRequest request = new LoginRequest(user.getUserName(), user.getPassword());
        LoginResponse response;
        LoginService service = new LoginService();

        response = service.login(request);

        assertNotNull(response);

        assertNotNull(response.getAuthToken());
        assertEquals("user", response.getUserName());
        assertNotNull(response.getPersonID());
        assertEquals(true, response.getSuccess());
        assertNull(response.getMessage());
    }

    @Test
    public void invalidUserNameFail() {
        // Test an incorrect userName
        LoginRequest request = new LoginRequest("otherUser", user.getPassword());
        LoginResponse response;
        LoginService service = new LoginService();

        response = service.login(request);

        assertNotNull(response);

        assertEquals(false, response.getSuccess());
        assertEquals("Error: Invalid userName or password" , response.getMessage());
        assertNull(response.getAuthToken());
        assertNull(response.getUserName());
        assertNull(response.getPersonID());
    }

    @Test
    public void invalidPasswordFail() {
        // Test an incorrect password
        LoginRequest request = new LoginRequest(user.getUserName(), "otherPassword");
        LoginResponse response;
        LoginService service = new LoginService();

        response = service.login(request);

        assertNotNull(response);

        assertEquals(false, response.getSuccess());
        assertEquals("Error: Invalid userName or password" , response.getMessage());
        assertNull(response.getAuthToken());
        assertNull(response.getUserName());
        assertNull(response.getPersonID());
    }

    @Test
    public void invalidEmptyUserNameFail() {
        // Test an incorrect userName
        LoginRequest request = new LoginRequest("", user.getPassword());
        LoginResponse response;
        LoginService service = new LoginService();

        response = service.login(request);

        assertNotNull(response);

        assertEquals(false, response.getSuccess());
        assertEquals("Error: Invalid request value" , response.getMessage());
        assertNull(response.getAuthToken());
        assertNull(response.getUserName());
        assertNull(response.getPersonID());
    }

    @Test
    public void invalidEmptyPasswordFail() {
        // Test an incorrect password
        LoginRequest request = new LoginRequest(user.getUserName(), "");
        LoginResponse response;
        LoginService service = new LoginService();

        response = service.login(request);

        assertNotNull(response);

        assertEquals(false, response.getSuccess());
        assertEquals("Error: Invalid request value" , response.getMessage());
        assertNull(response.getAuthToken());
        assertNull(response.getUserName());
        assertNull(response.getPersonID());
    }

}
