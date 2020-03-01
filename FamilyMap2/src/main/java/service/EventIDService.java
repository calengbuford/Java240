package service;

import dao.AuthTokenDao;
import dao.Database;
import dao.EventDao;
import model.AuthToken;
import model.Event;
import request.EventIDRequest;
import response.EventIDResponse;

import java.sql.Connection;

public class EventIDService {
    private AuthTokenDao authTokenDao;
    private EventDao eventDao;
    private Database db;
    private EventIDResponse response;
    private AuthToken token;

    public EventIDService() {
        response = new EventIDResponse();
    }

    /**
     * Returns the single Event object with the specified ID.
     * @param request
     * @param eventID
     * @return EventIDResponse
     * @throws Exception
     */
    private EventIDResponse event(EventIDRequest request, String eventID) throws Exception {
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

            // Get event with eventID from database
            eventDao = new EventDao(conn);
            Event event = eventDao.getEvent(eventID);

            db.closeConnection(true);
            response.setEvent(event);
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
