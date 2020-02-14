package dao;
import model.User;
import java.sql.*;

public class UserDao {

    /**
     * Empty constructor
     */
    public UserDao() { }

    /**
     * Create a user in the database
     * @param user the user object to create in the database
     */
    public void createUser(User user) {

    }

    /**
     * Return a user from the database
     * @param userID the userId to get from the database
     * @return User object
     */
    public User getUser(String userID) {
        return null;
    }

    /**
     * @param userName the userName to delete from the database
     * Remove the user from the database
     */
    public void deleteUser(String userName) { }

    /**
     * Remove the all users from the database
     */
    public void deleteAllUsers() { }

    /**
     * Update the user's table information
     */
    public void updateUser() { }

    /**
     * Check if a userName is valid
     * @param userName the userName to check for validity
     * @return if the user is valid
     */
    public Boolean isValidUser(String userName) {
        return true;
    }
}
