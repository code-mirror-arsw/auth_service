package com.code_room.auth_service.domain.Exception;


public class LoginException extends RuntimeException {

    private String userMessage;
    private String developerMessage;
    private final ErrorType errorType;

    public LoginException(ErrorType errorType) {
        super();
        this.errorType = errorType;
        this.setMessages(errorType);
    }

    private void setMessages(ErrorType errorType) {
        switch (errorType) {
            case USER_NOT_FOUND:
                this.userMessage = "User not found.";
                this.developerMessage = "No user exists with the given email or username.";
                break;
            case INVALID_PASSWORD:
                this.userMessage = "Invalid password.";
                this.developerMessage = "The password provided does not match the stored credentials.";
                break;
            case USER_NOT_VERIFIED:
                this.userMessage = "Account not verified.";
                this.developerMessage = "Attempt to log in with an account that has not been email-verified.";
                break;
            case ACCOUNT_DISABLED:
                this.userMessage = "Account is disabled.";
                this.developerMessage = "Attempt to log in with a disabled user account.";
                break;
            default:
                this.userMessage = "Login failed. Please try again.";
                this.developerMessage = "Unexpected error during login process.";
                break;
        }
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public enum ErrorType {
        USER_NOT_FOUND,
        INVALID_PASSWORD,
        USER_NOT_VERIFIED,
        ACCOUNT_DISABLED
    }

}
