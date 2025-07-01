package com.code_room.auth_service.domain.ports;

public interface SendEmailService {
    void sendRegistrationSuccessEmail(String to, String name, String verificationCode);

    void sendAlreadyVerifiedEmail(String to, String name);
}
