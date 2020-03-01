package service;

import dao.AuthTokenDao;
import dao.Database;
import dao.PersonDao;
import model.AuthToken;
import model.Person;
import request.PersonIDRequest;
import response.PersonIDResponse;

import java.sql.Connection;

public class PersonIDService {
    private AuthTokenDao authTokenDao;
    private PersonDao personDao;
    private Database db;
    private PersonIDResponse response;
    private AuthToken token;

    /**
     * Empty constructor
     */
    public PersonIDService() {
        response = new PersonIDResponse();
    }

    /**
     * Returns the single Person object with the specified ID
     * @return PersonResponse object as response
     * @throws Exception
     */
    public PersonIDResponse person(PersonIDRequest request, String personID) throws Exception {
        try {
            // Connect and make a new Dao
            db = new Database();
            Connection conn = db.openConnection();

            // Verify the AuthToken
            authTokenDao = new AuthTokenDao(conn);
            token = request.getAuthToken();
            if (authTokenDao.getAuthToken(token.getToken()) == null) {
                throw new Exception("AuthToken not valid");
            }

            // Get person with personID from database
            personDao = new PersonDao(conn);
            Person person = personDao.getPerson(personID);

            db.closeConnection(true);
            response.setPerson(person);
            response.setSuccess(true);
            return response;
        }
        catch (Exception e) {
            System.out.println("Internal server.Server Error\n" + e);
            db.closeConnection(false);
            response.setMessage(e.toString());
            response.setSuccess(false);
            return response;
        }
    }

}
