package service_;

import dao_.AuthTokenDao;
import dao_.Database;
import dao_.PersonDao;
import model_.AuthToken;
import model_.Person;
import request_.PersonRequest;
import response_.PersonResponse;

import java.sql.Connection;

public class PersonService {
    private AuthTokenDao authTokenDao;
    private PersonDao personDao;
    private Database db;
    private PersonResponse response;
    private Person[] personFamily;
    private AuthToken authToken;

    public PersonService() {
        response = new PersonResponse();
    }

    public PersonResponse person(PersonRequest request, String headerAuthToken) {
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

            // Get the userName based on the AuthToken
            String userName = authToken.getUserName();

            personDao = new PersonDao(conn);
            personFamily = personDao.getPersonFamily(userName);

            db.closeConnection(true);
            response.setData(personFamily);
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
