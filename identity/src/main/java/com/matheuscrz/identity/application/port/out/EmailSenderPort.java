package com.matheuscrz.identity.application.port.out;

public interface EmailSenderPort {
    void sendWelcomeEmail(String destinationEmail, String userName);

    void sendPasswordChangedEmail(String destinationEmail, String userName);
}