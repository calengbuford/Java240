package response_;

public class FillResponse {
    private Boolean success;
    private String message;

    /**
     * Empty constructor
     */
    public FillResponse() { }

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
