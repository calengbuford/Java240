package dao;
import model.Event;

public class EventDao {

    /**
     * Empty constructor
     */
    public EventDao() { }

    /**
     * Create an event in the database
     * @param event the event to create
     */
    public void createEvent(Event event) {

    }

    /**
     * Return an event from the database
     * @param eventID the event to return
     * @return the event
     */
    public Event getEvent(String eventID) {
        return null;
    }

    /**
     * Remove the event from the database
     * @param eventID the ID of the event to delete
     */
    public void deleteEvent(String eventID) { }

    /**
     * Remove the all events from the database
     */
    public void deleteAllEvents() { }

    /**
     * Update the event's table information
     */
    public void updateEvent() { }
}
