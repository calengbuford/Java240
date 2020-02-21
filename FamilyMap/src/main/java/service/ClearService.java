package service;

import dao.*;
import request.ClearRequest;
import response.ClearResponse;

import java.sql.Connection;

public class ClearService {
    private AuthTokenDao authTokenDao;
    private EventDao eventDao;
    private PersonDao personDao;
    private UserDao userDao;
    private Database db;

    /**
     * Empty constructor
     */
    public ClearService() {
    }

    /**
     * Take a ClearRequest, delete ALL data from the database, including user accounts, auth tokens, and
     * generated person and event data.
     * @param request the request information from the client
     * @return ClearResponse object as response
     * @throws Exception
     */
    public ClearResponse clear(ClearRequest request) throws Exception {
        try {
            // Connect and make a new Dao
            db = new Database();
            Connection conn = db.openConnection();

            authTokenDao = new AuthTokenDao(conn);
            eventDao = new EventDao(conn);
            personDao = new PersonDao(conn);
            userDao = new UserDao(conn);

            // Delete all information from the database
            authTokenDao.deleteAllAuthTokens();
            eventDao.deleteAllEvents();
            personDao.deleteAllPersons();
            userDao.deleteAllUsers();

            // Create the response with the AuthToken
            ClearResponse response = new ClearResponse();

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
