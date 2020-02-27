package response;

import model.Event;

public class EventIDResponse {
    private Event event;
    private Boolean success = null;
    private String message;

    /**
     * Empty constructor
     */
    public EventIDResponse() { }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
