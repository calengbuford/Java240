package request_;

import model_.Event;
import model_.Person;
import model_.User;

public class LoadRequest {
    private User[] users = null;
    private Person[] persons = null;
    private Event[] events = null;

    /**
     * Empty constructor
     */
    public LoadRequest() { }

    public User[] getUsers() {
        return users;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }

    public Person[] getPersons() {
        return persons;
    }

    public void setPersons(Person[] persons) {
        this.persons = persons;
    }

    public Event[] getEvents() {
        return events;
    }

    public void setEvents(Event[] events) {
        this.events = events;
    }
}
