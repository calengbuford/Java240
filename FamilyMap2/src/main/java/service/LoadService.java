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
    private int numPersonsAdded;
    private int numEventsAdded;

    /**
     * Empty constructor
     */
    public LoadService() {
        response = new LoadResponse();
    }

    /**
     * Clears all data from the database (just like the /clear API), and then loads the
     * posted user, person, and event data into the database.
     * @param request the request information from the client
     * @return LoadResponse object as response
     * @throws Exception
     */
    public LoadResponse load(LoadRequest request) throws Exception {
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
            }
            // Add the persons to the database
            Person[] persons = request.getPersons();
            for (Person person : persons) {
                personDao.createPerson(person);
            }
            // Add the users to the database
            User[] users = request.getUsers();
            for (User user : users) {
                userDao.createUser(user);
            }

            db.closeConnection(true);
            response.setMessage("Successfully added " + numPersonsAdded + " persons and " +
                    numEventsAdded + " events to the database.");
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
