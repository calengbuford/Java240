package service_;

import dao_.AuthTokenDao;
import dao_.Database;
import dao_.PersonDao;
import model_.AuthToken;
import model_.Person;
import request_.PersonIDRequest;
import response_.PersonIDResponse;

import java.sql.Connection;

public class PersonIDService {
    private AuthTokenDao authTokenDao;
    private PersonDao personDao;
    private Database db;
    private PersonIDResponse response;
    private Person person;
    private AuthToken authToken;

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
    public PersonIDResponse person(PersonIDRequest request, String headerAuthToken, String personID) {
        try {
            // Connect and make a new Dao
            db = new Database();
            Connection conn = db.openConnection();

            // Verify the AuthToken
            authTokenDao = new AuthTokenDao(conn);
            authToken = authTokenDao.getAuthTokenByToken(headerAuthToken);
            if (authToken == null) {
                throw new Exception("AuthToken not valid");
            }

            personDao = new PersonDao(conn);
            person = personDao.getPerson(personID);

            if (person == null) {
                throw new Exception("PersonID not valid");
            }

            // Check that the person belongs to this user
            String userName = authToken.getUserName();
            if (!person.getAssociatedUsername().equals(userName)) {
                throw new Exception("Requested person does not belong to this user");
            }

            db.closeConnection(true);
            response.setPersonFields(person);
            response.setSuccess(true);
            return response;
        }
        catch (Exception e) {
            System.out.println("Internal Server Error\n" + e);
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
