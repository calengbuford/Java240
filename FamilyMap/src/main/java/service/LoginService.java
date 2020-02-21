package service;

import dao.DataAccessException;
import dao.Database;
import dao.UserDao;
import model.AuthToken;
import model.User;
import request.LoginRequest;
import response.LoginResponse;

import java.sql.Connection;

public class LoginService {
    private User user;
    private AuthToken authToken;
    private UserDao userDao;
    private Database db;

    /**
     * Empty constructor
     */
    public LoginService() {
    }

    /**
     * Take a LoginRequest, log in the user and return an AuthToken.
     * @param request the request information from the client
     * @return LoginResponse object as response
     * @throws Exception
     */
    public LoginResponse login(LoginRequest request) throws Exception {
        // Check if the user is in the database
        try {
            // Connect and make a new Dao
            db = new Database();
            Connection conn = db.openConnection();
            userDao = new UserDao(conn);

            // Check if valid userName
            if (!userDao.isValidUser(request.getUserName())) {
                return null;
            }

            // Get the user from the database
            user = new User();
            user = userDao.getUser(request.getUserName());

            // Create a new AuthToken for the login session
            authToken = new AuthToken(user.getUserName(), user.getPassword());

            // Create the response with the AuthToken
            LoginResponse response = new LoginResponse();
            response.setAuthToken(authToken.getAuthToken());

            db.closeConnection(true);
            return response;
        }
        catch (Exception e) {
            System.out.println("Internal Server Error\n" + e);
            db.closeConnection(false);
            return null;
        }
    }
}
