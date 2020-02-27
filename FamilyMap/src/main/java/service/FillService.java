package service;

import dao.Database;
import dao.UserDao;
import response.FillResponse;

import java.sql.Connection;

public class FillService {
    private UserDao userDao;
    private Database db;
    private FillResponse response;
    private int numPersonsAdded;
    private int numEventsAdded;

    /**
     * Empty constructor
     */
    public FillService() {
        response = new FillResponse();
        numPersonsAdded = 0;
        numEventsAdded = 0;
    }

    /**
     * Populate the server's database with generated data for the specified user name.
     * The required "username" parameter must be a user already registered with the server. If there is
     * any data in the database already associated with the given user name, it is deleted. The
     * optional “generations” parameter lets the caller specify the number of generations of ancestors
     * to be generated, and must be a non-negative integer (the default is 4, which results in 31 new
     * persons each with associated events).
     * @param userName the userName for the user to be filled
     * @param generations the number of generations to fill
     * @return FillResponse object as response from fill
     * @throws Exception
     */
    public FillResponse fill(String userName, int generations) throws Exception {
        // Check if any information is already associated with userName
        try {
            // Connect and make a new Dao
            db = new Database();
            Connection conn = db.openConnection();

            userDao = new UserDao(conn);
            if (userDao.isValidUser(userName)) {
                userDao.deleteUser(userName);
                return null;
            }

            db.closeConnection(true);
            response.setMessage("Successfully added " + numPersonsAdded + " persons and " +
                                numEventsAdded + " events to the database.");
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
