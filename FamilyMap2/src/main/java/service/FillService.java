package service;

import com.google.gson.Gson;
import dao.*;
import data.*;
import model.Event;
import model.Person;
import model.User;
import request.FillRequest;
import response.FillResponse;

import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.util.Random;

public class FillService {
    private PersonDao personDao;
    private EventDao eventDao;
    private Database db;
    private FillResponse response;

    private FNameData femaleNames;
    private MNameData maleNames;
    private LNameData lastNames;
    private LocationData locData;

    private String userName = "";
    private String personID = "";
    private int numPersonsAdded;
    private int numEventsAdded;
    private int userBirthYear = 1990;


    /**
     * Constructor
     */
    public FillService() throws Exception {
        response = new FillResponse();

        numPersonsAdded = 0;
        numEventsAdded = 0;

        // Read in json files for data filling
        Gson gson = new Gson();
        Reader reader1 = new FileReader("json/fnames.json");
        femaleNames = gson.fromJson(reader1, FNameData.class);

        Reader reader2 = new FileReader("json/mnames.json");
        maleNames = gson.fromJson(reader2, MNameData.class);

        Reader reader3 = new FileReader("json/snames.json");
        lastNames = gson.fromJson(reader3, LNameData.class);

        Reader reader4 = new FileReader("json/locations.json");
        locData = gson.fromJson(reader4, LocationData.class);
    }

    /**
     * Populate the server's database with generated data for the specified user name.
     * The required "username" parameter must be a user already registered with the server. If there is
     * any data in the database already associated with the given user name, it is deleted. The
     * optional “generations” parameter lets the caller specify the number of generations of ancestors
     * to be generated, and must be a non-negative integer (the default is 4, which results in 31 new
     * persons each with associated events).
     * @param request FillRequest
     * @param userName the userName for the user to be filled
     * @param generations the number of generations to fill
     * @return FillResponse object as response from fill
     * @throws Exception
     */
    public FillResponse fill(FillRequest request, String userName, int generations) {
        // Check if any information is already associated with userName
        try {
            // Connect and make a new Dao
            db = new Database();
            Connection conn = db.openConnection();
            UserDao userDao = new UserDao(conn);
            eventDao = new EventDao(conn);
            personDao = new PersonDao(conn);

            // Get the user from the database
            this.userName = userName;
            User user = userDao.getUser(userName);
            if (user == null) {
                throw new Exception("Invalid userName");
            }
            this.personID = user.getPersonID();

            // Delete any information already associated with the user
            if (userDao.getUser(userName) != null) {
                eventDao.deleteEventByUserName(userName);
                personDao.deletePersonByUserName(userName);
            }

            // Fill user data
            Person person = new Person(user.getPersonID(), user.getUserName(), user.getFirstName(),
                    user.getLastName(), user.getGender());
            personDao.createPerson(person);
            numPersonsAdded++;
            setUserBirthEvent();
            createGenerations(person, this.userBirthYear, generations);

            db.closeConnection(true);
            response.setMessage("Successfully added " + numPersonsAdded + " persons and " +
                                numEventsAdded + " events to the database.");
            response.setSuccess(true);
            return response;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            try {
                db.closeConnection(false);
            }
            catch (Exception error) {
                System.out.println((error.getMessage()));
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

    private void setUserBirthEvent() throws Exception {
        // Get random location for event
        Location loc = getRandomLocation();

        // Set event details
        Event birth = new Event(this.userName, this.personID, loc.getLatitude(),
                loc.getLongitude(), loc.getCountry(), loc.getCity(), "birth", this.userBirthYear);
        eventDao.createEvent(birth);
        numEventsAdded++;
    }

    private void createGenerations(Person child, int yearChildBorn, int gensToMake) throws Exception {
        if (gensToMake == 0) {
            return;
        }

        // Create mother
        Person mother = new Person();
        mother.setAssociatedUsername(this.userName);
        mother.setFirstName(getRandomFName());
        mother.setLastName(getRandomLName());
        mother.setGender("f");
        String motherID = mother.getPersonID();

        // Create father
        Person father = new Person();
        father.setAssociatedUsername(this.userName);
        father.setFirstName(getRandomMName());
        father.setLastName(child.getLastName());
        father.setGender("m");
        String fatherID = father.getPersonID();

        // Set shared fields
        mother.setSpouseID(fatherID);
        father.setSpouseID(motherID);
        child.setMotherID(motherID);
        child.setFatherID(fatherID);

        // Update database
        personDao.updatePersonSpouse(motherID, fatherID);
        personDao.updatePersonSpouse(fatherID, motherID);
        personDao.updatePersonParents(child.getPersonID(), motherID, fatherID);


        // Create events

        Location loc;
        float latitude;
        float longitude;
        String country;
        String city;

        // Create birth events for mother and father: 18-22 years before children born

        // Mother birth
        loc = getRandomLocation();
        Event motherBirth = new Event(this.userName, motherID, loc.getLatitude(),
                loc.getLongitude(), loc.getCountry(), loc.getCity(), "birth", yearChildBorn - 20);

        // Father birth
        loc = getRandomLocation();
        Event fatherBirth = new Event(this.userName, fatherID, loc.getLatitude(),
                loc.getLongitude(), loc.getCountry(), loc.getCity(), "birth", yearChildBorn - 21);


        // Marriage: at age 17-27
        loc = getRandomLocation();
        Event motherMarriage = new Event(this.userName, motherID, loc.getLatitude(),
                loc.getLongitude(), loc.getCountry(), loc.getCity(), "marriage", yearChildBorn - 1);

        Event fatherMarriage = new Event(this.userName, fatherID, loc.getLatitude(),
                loc.getLongitude(), loc.getCountry(), loc.getCity(), "marriage", yearChildBorn - 1);


        // Death: at age 65-95
        int parentDeathYear = 0;
        if (2020 - yearChildBorn < 60) {
            parentDeathYear = 2019;
        }
        else {
            parentDeathYear  = yearChildBorn + 60;
        }

        loc = getRandomLocation();
        Event motherDeath = new Event(this.userName, motherID, loc.getLatitude(),
                loc.getLongitude(), loc.getCountry(), loc.getCity(), "death", parentDeathYear);

        Event fatherDeath = new Event(this.userName, fatherID, loc.getLatitude(),
                loc.getLongitude(), loc.getCountry(), loc.getCity(), "death", parentDeathYear - 4);


        // Add persons and events to database
        personDao.createPerson(mother);
        personDao.createPerson(father);
        eventDao.createEvent(motherBirth);
        eventDao.createEvent(fatherBirth);
        eventDao.createEvent(motherMarriage);
        eventDao.createEvent(fatherMarriage);
        eventDao.createEvent(motherDeath);
        eventDao.createEvent(fatherDeath);
        numPersonsAdded += 2;
        numEventsAdded += 6;

        createGenerations(mother, yearChildBorn - 20,gensToMake - 1);
        createGenerations(father, yearChildBorn - 21,gensToMake - 1);
    }

    private Location getRandomLocation() {
        // Get a random index within data array length
        Random random = new Random();
        int index = random.nextInt(this.locData.getData().length);
        return this.locData.getData()[index];
    }

    private String getRandomFName() {
        // Get a random index within data array length
        Random random = new Random();
        int index = random.nextInt(this.femaleNames.getData().length);
        return this.femaleNames.getData()[index];
    }

    private String getRandomMName() {
        // Get a random index within data array length
        Random random = new Random();
        int index = random.nextInt(this.maleNames.getData().length);
        return this.maleNames.getData()[index];
    }

    private String getRandomLName() {
        // Get a random index within data array length
        Random random = new Random();
        int index = random.nextInt(this.lastNames.getData().length);
        return this.lastNames.getData()[index];
    }

}
