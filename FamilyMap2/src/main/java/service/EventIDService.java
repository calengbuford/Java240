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
    private Event event;
    private AuthToken authToken;

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
    public EventIDResponse event(EventIDRequest request, String headerAuthToken, String eventID) {
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

            eventDao = new EventDao(conn);
            event = eventDao.getEvent(eventID);

            if (event == null) {
                throw new Exception("EventID not valid");
            }

            // Check that the event belongs to this user
            String userName = authToken.getUserName();
            if (!event.getAssociatedUsername().equals(userName)) {
                throw new Exception("Requested event does not belong to this user");
            }

            db.closeConnection(true);
            response.setEventFields(event);
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
