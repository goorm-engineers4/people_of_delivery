package com.example.cloudfour.peopleofdelivery.global.auth.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text) throws MessagingException;
}