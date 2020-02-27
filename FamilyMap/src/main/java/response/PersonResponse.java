package response;

import model.Person;

public class PersonResponse {
    private Person[] data;
    private Boolean success = null;
    private String message;

    /**
     * Empty constructor
     */
    public PersonResponse() { }

    public Person[] getData() {
        return data;
    }

    public void setData(Person[] data) {
        this.data = data;
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
