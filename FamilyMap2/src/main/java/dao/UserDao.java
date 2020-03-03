package dao;

import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    private final Connection conn;

    /**
     * Constructor
     */
    public UserDao(Connection conn)
    {
        this.conn = conn;
    }


    /**
     * Create a user in the database
     * @param user the user object to create in the database
     * @throws DataAccessException
     */
    public void createUser(User user) throws DataAccessException {
        String sql = "INSERT INTO Users (userName, password, email, firstName, lastName, " +
                " gender, personID) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setString(6, user.getGender());
            stmt.setString(7, user.getPersonID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    /**
     * Return a user from the database
     * @param userName the userName to get from the database
     * @return User object
     * @throws DataAccessException
     */
    public User getUser(String userName) throws DataAccessException {
        User user;
        ResultSet rs = null;
        String sql = "SELECT * FROM Users WHERE userName = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getString("userName"), rs.getString("password"),
                                rs.getString("email"), rs.getString("firstName"),
                                rs.getString("lastName"), rs.getString("gender"),
                                rs.getString("personID"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding user");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * @param userName the userName to delete from the database
     * Remove the user from the database
     */
    public void deleteUser(String userName) throws DataAccessException {
        String sql = "DELETE FROM Users WHERE userName = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while deleting from the database");
        }
    }

    /**
     * Remove the all users from the database
     * @throws DataAccessException
     */
    public void deleteAllUsers() throws DataAccessException {
        String sql = "DELETE FROM Users";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while deleting from the database");
        }
    }

    /**
     * Update the user's table information
     */
    public void updateUser() { }
}
