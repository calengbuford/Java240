package dao_;

import model_.AuthToken;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuthTokenDao {
    private final Connection conn;

    /**
     * Constructor
     */
    public AuthTokenDao(Connection conn)
    {
        this.conn = conn;
    }

    /**
     * Create an authToken in the database
     * @param authToken the authToken to create
     * @throws DataAccessException
     */
    public void createAuthToken(AuthToken authToken) throws DataAccessException {
        String sql = "INSERT INTO AuthTokens (token, userName, password) VALUES(?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken.getToken());
            stmt.setString(2, authToken.getUserName());
            stmt.setString(3, authToken.getPassword());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    /**
     * Get an AuthToken from the database
     * @param token the authToken associated with the AuthToken object
     * @return the AuthToken object
     * @throws DataAccessException
     */
    public AuthToken getAuthTokenByToken(String token) throws DataAccessException {
        AuthToken authToken;
        ResultSet rs = null;
        String sql = "SELECT * FROM AuthTokens WHERE token = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            rs = stmt.executeQuery();
            if (rs.next()) {
                authToken = new AuthToken(rs.getString("token"), rs.getString("userName"),
                        rs.getString("password"));
                return authToken;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding authToken");
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
     * Get an AuthToken from the database
     * @param userName associated with the AuthToken object
     * @return a List of AuthToken objects
     * @throws DataAccessException
     */
    public List<AuthToken> getAuthTokenByUserName(String userName) throws DataAccessException {
        AuthToken authToken;
        ResultSet rs = null;
        String sql = "SELECT * FROM AuthTokens WHERE userName = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            List authTokens = new ArrayList<AuthToken>();
            while (rs.next()) {
                authToken = new AuthToken(rs.getString("token"), rs.getString("userName"),
                        rs.getString("password"));
                authTokens.add(authToken);
            }
            if (authTokens.size() > 0) {
                return authTokens;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding authToken");
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
     * Remove the all authTokens from the database
     * @throws DataAccessException
     */
    public void deleteAllAuthTokens() throws DataAccessException {
        String sql = "DELETE FROM AuthTokens";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while deleting from the database");
        }
    }

}
