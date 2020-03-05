package dao_;

import model_.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersonDao {
    private final Connection conn;

    /**
     * Constructor
     */
    public PersonDao(Connection conn)
    {
        this.conn = conn;
    }

    /**
     *
     * @param person the person to create in the database
     * @throws DataAccessException
     */
    public void createPerson(Person person) throws DataAccessException {
        String sql = "INSERT INTO Persons (personID, associatedUsername, firstName, lastName, gender," +
                " fatherID, motherID, spouseID) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, person.getPersonID());
            stmt.setString(2, person.getAssociatedUsername());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4, person.getLastName());
            stmt.setString(5, person.getGender());
            stmt.setString(6, person.getFatherID());
            stmt.setString(7, person.getMotherID());
            stmt.setString(8, person.getSpouseID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    /**
     *
     * @param personID the ID of the person
     * @return the person with personID
     * @throws DataAccessException
     */
    public Person getPerson(String personID) throws DataAccessException {
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE personID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                person = new Person(rs.getString("personID"), rs.getString("associatedUsername"),
                        rs.getString("firstName"), rs.getString("lastName"),
                        rs.getString("gender"), rs.getString("fatherID"),
                        rs.getString("motherID"), rs.getString("spouseID"));
                return person;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding person");
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
     * Recursively get all family members of user with associatedUsername
     * @param associatedUsername
     * @return Person array of all people in the associatedUsername's family
     * @throws DataAccessException
     */
    public Person[] getPersonFamily(String associatedUsername) throws DataAccessException {
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE associatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, associatedUsername);
            rs = stmt.executeQuery();
            List<Person> personsList = new ArrayList<Person>();
            while (rs.next()) {
                person = new Person(rs.getString("personID"), rs.getString("associatedUsername"),
                        rs.getString("firstName"), rs.getString("lastName"),
                        rs.getString("gender"), rs.getString("fatherID"),
                        rs.getString("motherID"), rs.getString("spouseID"));
                personsList.add(person);
            }
            if (personsList.size() > 0) {
                Person[] persons = new Person[personsList.size()];
                for (int i = 0; i < personsList.size(); i++) {
                    persons[i] = personsList.get(i);
                }
                return persons;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding person");
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
     * Remove the person from the database
     * @param associatedUsername
     */
    public void deletePersonByUserName(String associatedUsername) throws DataAccessException {
        String sql = "DELETE FROM Persons WHERE associatedUsername = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, associatedUsername);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while deleting from the database");
        }
    }

    /**
     * Remove the all persons from the database
     * @throws DataAccessException
     */
    public void deleteAllPersons() throws DataAccessException {
        String sql = "DELETE FROM Persons";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while deleting from the database");
        }
    }

    /**
     * Update the person's parentIDs
     * @throws DataAccessException
     */
    public void updatePersonParents(String personID, String motherID, String fatherID) throws DataAccessException {
        String sql = "UPDATE PERSONS SET fatherID = ?, motherID = ? WHERE personID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fatherID);
            stmt.setString(2, motherID);
            stmt.setString(3, personID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while deleting from the database");
        }
    }

    /**
     * Update the person's spouseID
     * @throws DataAccessException
     */
    public void updatePersonSpouse(String personID, String spouseID) throws DataAccessException {
        String sql = "UPDATE PERSONS SET spouseID = ? WHERE personID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, spouseID);
            stmt.setString(2, personID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while deleting from the database");
        }
    }
}
