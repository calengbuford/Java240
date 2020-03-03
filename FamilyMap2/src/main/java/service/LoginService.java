package service;

import dao.AuthTokenDao;
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
    private AuthTokenDao authTokenDao;
    private Database db;
    private LoginResponse response;

    /**
     * Empty constructor
     */
    public LoginService() {
        response = new LoginResponse();
    }

    /**
     * Take a LoginRequest, log in the user and return an AuthToken.
     * @param request the request information from the client
     * @return LoginResponse object as response
     */
    public LoginResponse login(LoginRequest request) {
        try {
            // Connect and make a new Dao
            db = new Database();
            Connection conn = db.openConnection();
            userDao = new UserDao(conn);
            authTokenDao = new AuthTokenDao(conn);

            // Check if valid userName
            if (userDao.getUser(request.getUserName()) == null) {
                throw new Exception("Invalid userName");
            }

            // Get the user from the database
            user = new User();
            user = userDao.getUser(request.getUserName());

            // Create a new AuthToken for the login session
            authToken = new AuthToken(user.getUserName(), user.getPassword());

            // Create a new AuthToken for the login session
            authToken = new AuthToken(user.getUserName(), user.getPassword());
            authTokenDao = new AuthTokenDao(conn);
            authTokenDao.createAuthToken(authToken);

            // Add AuthToken to the response
            db.closeConnection(true);
            response.setAuthToken(authToken.getToken());
            response.setUserName(user.getUserName());
            response.setPersonID(user.getPersonID());
            response.setSuccess(true);
            return response;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            try {
                db.closeConnection(false);
            }
            catch (Exception error) {
                System.out.println(error.getMessage());
            }
            if (e.getMessage() == null) {
                response.setMessage("Internal Server Error");
            }
            else {
                response.setMessage(e.getMessage());
            }
            response.setSuccess(false);
            return response;
        }
    }
}
