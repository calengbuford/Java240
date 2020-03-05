package response_;

public class ClearResponse {
    private Boolean success = null;
    private String message = null;

    /**
     * Empty constructor
     */
    public ClearResponse() { }

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
