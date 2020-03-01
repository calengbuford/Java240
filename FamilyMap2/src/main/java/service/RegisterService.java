package service;

import dao.Database;
import dao.UserDao;
import model.AuthToken;
import model.User;
import request.RegisterRequest;
import response.RegisterResponse;

import java.sql.Connection;
import java.util.UUID;

public class RegisterService {
    private User user;
    private AuthToken authToken;
    private UserDao userDao;
    private Database db;
    private RegisterResponse response;

    /**
     * Empty constructor
     */
    public RegisterService() {
        response = new RegisterResponse();
    }

    /**
     * Take a RegisterRequest, create a new user account, generate 4 generations of ancestor data for the new
     * user, log the user in, and return a response with an AuthToken.
     * @param request the request information from the client
     * @return RegisterResponse object as response
     * @throws Exception
     */
    public RegisterResponse register(RegisterRequest request) throws Exception {
        try {
            // Connect and make a new Dao
            db = new Database();
            Connection conn = db.openConnection();

            // Build a new user
            user = new User();
            user.setUserName(request.getUserName());
            user.setPassword(request.getPassword());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setGender(request.getGender());
            user.setPersonID(UUID.randomUUID().toString());

            userDao = new UserDao(conn);
            if (userDao.isValidUser(user.getUserName())) {
                userDao.deleteUser(user.getUserName());
                throw new Exception("User already exists in database");
            }

            // Fill the user with random information
            FillService fillService = new FillService();
            int generations = 4;
            fillService.fill(user.getUserName(), generations);

            // Add the user the database
            userDao.createUser(user);

            // Create a new AuthToken for the login session
            authToken = new AuthToken(user.getUserName(), user.getPassword());

            db.closeConnection(true);
            response.setAuthToken(authToken.getToken());
            response.setUserName(user.getUserName());
            response.setPersonID(user.getPersonID());
            response.setSuccess(true);
            return response;
        }
        catch (Exception e) {
            System.out.println("Internal server.Server Error\n" + e);
            db.closeConnection(false);
            response.setMessage(e.toString());
            response.setSuccess(false);
            return null;
        }
    }
}
