package service_;

import dao_.AuthTokenDao;
import dao_.Database;
import dao_.UserDao;
import model_.AuthToken;
import model_.User;
import request_.LoginRequest;
import response_.LoginResponse;

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

            // Check request parameters
            if (request.getUserName().isEmpty() ||  request.getPassword().isEmpty()) {
                throw new Exception("Invalid request value");
            }

            // Check if valid userName
            if (userDao.getUser(request.getUserName()) == null) {
                throw new Exception("Invalid userName or password");
            }

            // Get the user from the database
            user = new User();
            user = userDao.getUser(request.getUserName());

            if (!user.getPassword().equals(request.getPassword())) {
                throw new Exception("Invalid userName or password");
            }

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
            System.out.println("Error: " + e.getMessage());
            try {
                db.closeConnection(false);
            }
            catch (Exception error) {
                System.out.println("Error: " + error.getMessage());
            }
            if (e.getMessage() == null) {
                response.setMessage("Internal Server Error");
            }
            else {
                response.setMessage("Error: " + e.getMessage());
            }
            response.setSuccess(false);
            return response;
        }
    }
}
