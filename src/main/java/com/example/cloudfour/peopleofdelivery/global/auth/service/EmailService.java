package com.example.cloudfour.peopleofdelivery.global.auth.service;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);
}