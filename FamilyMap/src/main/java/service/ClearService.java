package service;

import dao.AuthTokenDao;
import dao.EventDao;
import dao.PersonDao;
import dao.UserDao;
import request.ClearRequest;
import response.ClearResponse;

public class ClearService {
    private AuthTokenDao authTokenDao = null;
    private EventDao eventDao = null;
    private PersonDao personDao = null;
    private UserDao userDao = null;

    /**
     * Empty constructor
     */
    public ClearService() {
    }

    /**
     * Take a ClearRequest, delete ALL data from the database, including user accounts, auth tokens, and
     * generated person and event data.
     * @return ClearResponse object as response from clear
     */
    public ClearResponse clear(ClearRequest request) {
        authTokenDao = new AuthTokenDao();
        eventDao = new EventDao();
        personDao = new PersonDao();
        userDao = new UserDao();

        // Delete all information from the database
        authTokenDao.deleteAllAuthTokens();
        eventDao.deleteAllEvents();
        personDao.deleteAllPersons();
        userDao.deleteAllUsers();

        // Create the response with the AuthToken
        ClearResponse response = new ClearResponse();

        return response;
    }
}
