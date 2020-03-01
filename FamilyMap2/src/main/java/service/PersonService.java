package service;

import dao.AuthTokenDao;
import dao.Database;
import dao.PersonDao;
import model.AuthToken;
import model.Person;
import request.PersonRequest;
import response.PersonResponse;

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

    private PersonResponse person(PersonRequest request) throws Exception {
        try {
            // Connect and make a new Dao
            db = new Database();
            Connection conn = db.openConnection();

            // Verify the AuthToken
            authTokenDao = new AuthTokenDao(conn);
            authToken = authTokenDao.getAuthToken(request.getAuthToken().getToken());
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
            db.closeConnection(false);
            response.setMessage(e.toString());
            response.setSuccess(false);
            return response;
        }
    }

}
