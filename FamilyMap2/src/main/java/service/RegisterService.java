package service;

import dao.AuthTokenDao;
import dao.Database;
import dao.PersonDao;
import dao.UserDao;
import model.AuthToken;
import model.Person;
import model.User;
import request.FillRequest;
import request.RegisterRequest;
import response.FillResponse;
import response.RegisterResponse;

import java.sql.Connection;
import java.util.UUID;

public class RegisterService {
    private User user;
    private AuthToken authToken;
    private UserDao userDao;
    private AuthTokenDao authTokenDao;
    private Database db;
    private RegisterResponse response;

    /**
     * Constructor
     */
    public RegisterService() {
        response = new RegisterResponse();
    }

    /**
     * Take a RegisterRequest, create a new user account, generate 4 generations of ancestor data for the new
     * user, log the user in, and return a response with an AuthToken.
     * @param request the request information from the client
     * @return RegisterResponse object as response
     */
    public RegisterResponse register(RegisterRequest request) {
        try {
            // Connect and make a new Dao
            db = new Database();
            Connection conn = db.openConnection();

            // Build a new user
            user = new User(request.getUserName(), request.getPassword(), request.getEmail(),
                    request.getFirstName(), request.getLastName(), request.getGender(), UUID.randomUUID().toString());

            userDao = new UserDao(conn);
            if (userDao.getUser(user.getUserName()) != null) {
                throw new Exception("User already exists in database");
            }

            // Add the user to the database
            userDao.createUser(user);

            // Create a new AuthToken for the login session
            authToken = new AuthToken(user.getUserName(), user.getPassword());
            authTokenDao = new AuthTokenDao(conn);
            authTokenDao.createAuthToken(authToken);

            db.closeConnection(true);

            // Fill the user with random information
            FillService fillService = new FillService();
            FillRequest fillRequest = new FillRequest();
            int generations = 4;
            FillResponse fillResponse = fillService.fill(fillRequest, user.getUserName(), generations);

            if (!fillResponse.getSuccess()) {
                throw new Exception(fillResponse.getMessage());
            }

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
