package response;

import model.Person;

public class PersonIDResponse {
    private Person person;
    private Boolean success = null;
    private String message;

    /**
     * Empty constructor
     */
    public PersonIDResponse() { }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
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
