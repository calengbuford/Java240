package service;

import dao.AuthTokenDao;
import dao.Database;
import dao.EventDao;
import model.AuthToken;
import model.Event;
import request.EventRequest;
import response.EventResponse;

import java.sql.Connection;
import java.util.List;

public class EventService {
    private AuthTokenDao authTokenDao;
    private EventDao eventDao;
    private Database db;
    private EventResponse response;
    private Event[] personEvents;
    private AuthToken authToken;

    public EventService() {
        response = new EventResponse();
    }

    public EventResponse event(EventRequest request, String headerAuthToken) {
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

            eventDao = new EventDao(conn);
            personEvents = eventDao.getPersonEvents(userName);

            db.closeConnection(true);
            response.setData(personEvents);
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
