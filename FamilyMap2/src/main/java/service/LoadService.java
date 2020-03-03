package service;

import dao.*;
import model.Event;
import model.Person;
import model.User;
import request.LoadRequest;
import response.LoadResponse;

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

            // Add the events to the database
            Event[] events = request.getEvents();
            for (Event event : events) {
                eventDao.createEvent(event);
                numEventsAdded++;
            }
            // Add the persons to the database
            Person[] persons = request.getPersons();
            for (Person person : persons) {
                personDao.createPerson(person);
                numPersonsAdded++;
            }
            // Add the users to the database
            User[] users = request.getUsers();
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
            System.out.println(e.getMessage());
            try {
                db.closeConnection(false);
            }
            catch (Exception error) {
                System.out.println(error.getMessage());
            }
            if (e.getMessage() == null) {
                response.setMessage("Internal Server Error");
            }
            else {
                response.setMessage(e.getMessage());
            }
            response.setSuccess(false);
            return response;
        }
    }
}
