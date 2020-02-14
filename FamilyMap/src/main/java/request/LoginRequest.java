package request;

public class LoginRequest {
    private String userName = null;
    private String password = null;

    /**
     * Empty constructor
     */
    public LoginRequest() { }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
