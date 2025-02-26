package service_;

import dao_.*;
import model_.Event;
import model_.Person;
import model_.User;
import request_.LoadRequest;
import response_.LoadResponse;

import java.sql.Connection;

public class LoadService {
    private AuthTokenDao authTokenDao;
    private EventDao eventDao;
    private PersonDao personDao;
    private UserDao userDao;
    private Database db;
    private LoadResponse response;
    private int numUsersAdded;
    private int numPersonsAdded;
    private int numEventsAdded;

    /**
     * Constructor
     */
    public LoadService() {
        response = new LoadResponse();
        numUsersAdded = 0;
        numPersonsAdded = 0;
        numEventsAdded = 0;
    }

    /**
     * Clears all data from the database (just like the /clear API), and then loads the
     * posted user, person, and event data into the database.
     * @param request the request information from the client
     * @return LoadResponse object as response
     */
    public LoadResponse load(LoadRequest request) {
        try {
            // Connect and make a new Dao
            db = new Database();
            Connection conn = db.openConnection();

            // Initialize dao objects
            authTokenDao = new AuthTokenDao(conn);
            eventDao = new EventDao(conn);
            personDao = new PersonDao(conn);
            userDao = new UserDao(conn);

            // Delete all information from the database
            authTokenDao.deleteAllAuthTokens();
            eventDao.deleteAllEvents();
            personDao.deleteAllPersons();
            userDao.deleteAllUsers();

            Event[] events = request.getEvents();
            Person[] persons = request.getPersons();
            User[] users = request.getUsers();

            if (events == null || persons == null || users == null) {
                throw new Exception("Missing values");
            }

            // Add the events to the database
            for (Event event : events) {
                eventDao.createEvent(event);
                numEventsAdded++;
            }
            // Add the persons to the database
            for (Person person : persons) {
                personDao.createPerson(person);
                numPersonsAdded++;
            }
            // Add the users to the database
            for (User user : users) {
                userDao.createUser(user);
                numUsersAdded++;
            }

            db.closeConnection(true);
            response.setMessage("Successfully added " + numUsersAdded + " users, " + numPersonsAdded +
                    " persons, and " + numEventsAdded + " events to the database.");
            response.setSuccess(true);
            return response;
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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
