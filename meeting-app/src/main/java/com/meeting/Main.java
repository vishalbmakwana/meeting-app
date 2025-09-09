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
import java.util.List;

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
            
            LocalDateTime meeting1Time = LocalDateTime.of(2025, 9, 15, 10, 0);
            LocalDateTime meeting2Time = LocalDateTime.of(2025, 9, 15, 14, 0);

            Meeting meeting1 = meetingService.createMeeting(
                "Standup", meeting1Time, alice1, Arrays.asList(alice2, alice3)
            );
            System.out.println("Created: " + meeting1.getTitle() + " at " + meeting1.getStartTime().format(FORMATTER));
            Meeting meeting2 = meetingService.createMeeting(
                "Project Review", meeting2Time, alice2, Arrays.asList(alice1, alice4)
            );
            System.out.println("Created: " + meeting2.getTitle() + " at " + meeting2.getStartTime().format(FORMATTER));
            
            // invalid time - min
            try {
                LocalDateTime invalidTime = LocalDateTime.of(2025, 9, 11, 10, 30);
                meetingService.createMeeting("Invalid", invalidTime, alice1, Arrays.asList(alice2));
            } catch (IllegalArgumentException e) {
                System.out.println("invalid validation: " + e.getMessage());
            }
            // time conflict
            try {
                LocalDateTime invalidTime = LocalDateTime.of(2025, 9, 15, 10, 0);
                meetingService.createMeeting("Conflict", invalidTime, alice1, Arrays.asList(alice2));
            } catch (IllegalArgumentException e) {
                System.out.println("time conflict : " + e.getMessage());
            }
            // schedules - upcoming meetings
            List<Meeting> alice1Schedule = meetingService.getUpcomingMeetingsForPerson(alice1);
            System.out.println("upcoming (" + alice1Schedule.size() + "):");
            for (Meeting meeting : alice1Schedule) {
                System.out.println(meeting.getTitle() + " at " +
                    meeting.getStartTime().format(FORMATTER) + " (Organizer: " + 
                    meeting.getOrganizer().getName() + ")");
            }
            
         
            
            // Suggest available slots
            LocalDateTime searchStart = LocalDateTime.of(2025, 9, 15, 8, 0);
            LocalDateTime searchEnd = LocalDateTime.of(2025, 9, 15, 18, 0);
            List<Person> participants = Arrays.asList(alice1, alice2, alice3);
            
            List<LocalDateTime> suggestions = meetingService.suggestAvailableTimeSlots(
                participants, searchStart, searchEnd, 5
            );
            
            System.out.println("Available slots:");
            for (LocalDateTime slot : suggestions) {
                System.out.println("     - " + slot.format(FORMATTER));
            }
        } catch (Exception e) {
            System.err.println("Demo failed with error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}