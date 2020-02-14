package service;

import dao.UserDao;
import model.AuthToken;
import model.User;
import request.LoginRequest;
import response.LoginResponse;

public class LoginService {
    private User user = null;
    private AuthToken authToken = null;
    private UserDao userDao = null;

    /**
     * Empty constructor
     */
    public LoginService() {
    }

    /**
     * Take a LoginRequest, log in the user and return an AuthToken.
     * @return LoginResponse object as response from clear
     */
    public LoginResponse login(LoginRequest request) {
        // Check if the user is in the database
        userDao = new UserDao();
        if (!userDao.isValidUser(request.getUserName())) {
            return null;
        }
        // Get the user from the database
        user = new User();
        userDao.getUser(request.getUserName());

        // Create a new AuthToken for the login session
        authToken = new AuthToken(user.getUserName(), user.getPassword());

        // Create the response with the AuthToken
        LoginResponse response = new LoginResponse();
        response.setAuthToken(authToken.getAuthToken());

        return response;
    }
}
