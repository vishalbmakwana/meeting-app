package com.meeting;

import com.meeting.entity.Meeting;
import com.meeting.entity.Person;
import com.meeting.service.MeetingService;
import com.meeting.service.PersonService;
import com.meeting.service.impl.MeetingServiceImpl;
import com.meeting.service.impl.PersonServiceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class Main {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public static void main(String[] args) {
        System.out.println("=== Scheduling Application Demo ===\n");

        PersonService personService = new PersonServiceImpl();
        MeetingService meetingService = new MeetingServiceImpl(personService);
        try {
            System.out.println("Creating persons with unique email validation:");
            Person alice1 = personService.createPerson("Alice1 Person1", "alice1@company.com");
            Person alice2 = personService.createPerson("Alice2 Person2", "alice2@company.com");
            Person alice3 = personService.createPerson("Alice3 Person3", "alice3@company.com");
            Person alice4 = personService.createPerson("Alice4 Person4", "alice4@company.com");
            
            System.out.println("Created 1: " + alice1);
            System.out.println("Created 2: " + alice2);
            System.out.println("Created 3: " + alice3);
            System.out.println("Created 4: " + alice4);
            // duplicate
            try {
                personService.createPerson("Alice Duplicate", "alice1@company.com");
            } catch (IllegalArgumentException e) {
                System.out.println("Duplicate email validation: " + e.getMessage());
            }
            
            LocalDateTime meeting1Time = LocalDateTime.of(2024, 12, 15, 10, 0);
            Meeting meeting1 = meetingService.createMeeting(
                "Standup", meeting1Time, alice1, Arrays.asList(alice2, alice3)
            );
            System.out.println("Created: " + meeting1.getTitle() + " at " + meeting1.getStartTime().format(FORMATTER));
            
        } catch (Exception e) {
            System.err.println("Demo failed with error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}