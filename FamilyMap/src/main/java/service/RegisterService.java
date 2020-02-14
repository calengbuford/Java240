package service;

import dao.UserDao;
import model.AuthToken;
import model.User;
import request.RegisterRequest;
import request.FillRequest;
import response.RegisterResponse;
import service.FillService;

import java.util.UUID;

public class RegisterService {
    private User user = null;
    private AuthToken authToken = null;
    private UserDao userDao = null;

    /**
     * Empty constructor
     */
    public RegisterService() {
    }

    /**
     * Take a RegisterRequest, create a new user account, generate 4 generations of ancestor data for the new
     * user, log the user in, and return a response with an AuthToken.
     * @return RegisterResponse object as response from clear
     */
    public RegisterResponse register(RegisterRequest request) {
        // Build a new user
        user = new User();
        user.setUserName(request.getUserName());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setGender(request.getGender());
        user.setPersonID(UUID.randomUUID().toString());

        // Fill the user with random information
        FillRequest fillRequest = new FillRequest();
        FillService fillService = new FillService();
        int generations = 4;
        fillService.fill(fillRequest, user.getUserName(), generations);

        // Add the user the database
        userDao = new UserDao();
        userDao.createUser(user);

        // Create a new AuthToken for the login session
        authToken = new AuthToken(user.getUserName(), user.getPassword());

        // Create the response with the AuthToken
        RegisterResponse response = new RegisterResponse();
        response.setAuthToken(authToken.getAuthToken());

        return response;
    }
}
