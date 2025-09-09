package com.meeting.controller;


import com.meeting.dto.MeetingDTO;
import com.meeting.entity.Meeting;
import com.meeting.entity.Person;
import com.meeting.service.MeetingService;
import com.meeting.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class MeetingController {

    private final MeetingService meetingService;
    private final PersonService personService;

    @PostMapping
    public ResponseEntity<?> createMeeting(@Valid @RequestBody MeetingDTO meetingDTO) {
        try {
            // Find organizer
            Optional<Person> organizer = personService.findByEmail(meetingDTO.getOrganizerEmail());
            if (organizer.isEmpty()) {
                return ResponseEntity.badRequest().body("Organizer with email " + meetingDTO.getOrganizerEmail() + " not found");
            }

            // Find attendees
            List<Person> attendees = new ArrayList<>();
            for (String email : meetingDTO.getAttendeeEmails()) {
                Optional<Person> attendee = personService.findByEmail(email);
                if (attendee.isEmpty()) {
                    return ResponseEntity.badRequest().body("Attendee with email " + email + " not found");
                }
                attendees.add(attendee.get());
            }

            Meeting meeting = meetingService.createMeeting(
                    meetingDTO.getTitle(),
                    meetingDTO.getStartTime(),
                    organizer.get(),
                    attendees
            );

            MeetingDTO responseDTO = MeetingDTO.builder()
                    .uuid(meeting.getUuid())
                    .title(meeting.getTitle())
                    .startTime(meeting.getStartTime())
                    .endTime(meeting.getEndTime())
                    .organizerEmail(meeting.getOrganizer().getEmail())
                    .attendeeEmails(meeting.getAttendees().stream().map(Person::getEmail).toList())
                    .build();

            log.info("Created meeting: {}", meeting.getTitle());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (IllegalArgumentException e) {
            log.error("Failed to create meeting: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Meeting>> getAllMeetings() {
        List<Meeting> meetings = meetingService.getAllMeetings();
        log.info("Retrieved {} meetings", meetings.size());
        return ResponseEntity.ok(meetings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMeetingById(@PathVariable String id) {
        Optional<Meeting> meeting = meetingService.findById(id);
        if (meeting.isPresent()) {
            return ResponseEntity.ok(meeting.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/schedule/{uuid}")
    public ResponseEntity<?> getPersonSchedule(@PathVariable String uuid) {
        Optional<Person> person = personService.findById(uuid);
        if (person.isEmpty()) {
            return ResponseEntity.badRequest().body("Person with uuid " + uuid + " not found");
        }

        List<Meeting> upcomingMeetings = meetingService.getUpcomingMeetingsForPerson(person.get());
        log.info("Retrieved {} upcoming meetings for {}", upcomingMeetings.size(), person.get().getEmail());
        return ResponseEntity.ok(upcomingMeetings);
    }

    @GetMapping("/suggest-slots")
    public ResponseEntity<?> suggestTimeSlots(
            @RequestParam List<String> emails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "5") int maxSuggestions) {
        try {
            // Find all persons
            List<Person> persons = new ArrayList<>();
            for (String email : emails) {
                Optional<Person> person = personService.findByEmail(email);
                if (person.isEmpty()) {
                    return ResponseEntity.badRequest().body("Person with email " + email + " not found");
                }
                persons.add(person.get());
            }
            List<LocalDateTime> suggestions = meetingService.suggestAvailableTimeSlots(
                    persons, startDate, endDate, maxSuggestions);
            log.info("Found {} available time slots for {} persons", suggestions.size(), persons.size());
            return ResponseEntity.ok(suggestions);
        } catch (IllegalArgumentException e) {
            log.error("Failed to suggest time slots: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
