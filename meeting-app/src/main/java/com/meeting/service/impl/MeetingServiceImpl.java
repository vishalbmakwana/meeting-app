package com.meeting.service.impl;

import com.meeting.entity.Meeting;
import com.meeting.entity.Person;
import com.meeting.service.MeetingService;
import com.meeting.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingServiceImpl implements MeetingService {

    private final PersonService personService;
    
    // In-memory storage for meetings
    private final Map<String, Meeting> meetingsById = new ConcurrentHashMap<>();
    private final List<Meeting> meetings = Collections.synchronizedList(new ArrayList<>());

    @Override
    public Meeting createMeeting(String title, LocalDateTime startTime, Person organizer, List<Person> attendees) {
        // Validate that all persons involved exist in the system
        List<Person> allParticipants = new ArrayList<>(attendees);
        allParticipants.add(organizer);
        
        for (Person person : allParticipants) {
            if (!personService.emailExists(person.getEmail())) {
                throw new IllegalArgumentException("Person with email " + person.getEmail() + " does not exist in the system");
            }
        }
        
        // Check for scheduling conflicts
        if (!isTimeSlotAvailable(allParticipants, startTime)) {
            throw new IllegalArgumentException("One or more participants have a scheduling conflict at " + startTime);
        }
        
        Meeting meeting = new Meeting(title.trim(), startTime, organizer, attendees);
        meetingsById.put(meeting.getUuid(), meeting);
        meetings.add(meeting);
        
        log.info("Successfully created meeting: {}", meeting.getUuid());
        return meeting;
    }

    @Override
    public boolean isTimeSlotAvailable(List<Person> persons, LocalDateTime startTime) {
        if (persons == null || persons.isEmpty() || startTime == null) {
            return false;
        }
        
        LocalDateTime endTime = startTime.plusHours(1);
        
        return meetings.stream()
                .filter(meeting -> meeting.involvesAnyPerson(persons))
                // to ensure no meeting existing during suggested startTime and endTime
                // example - startTime 2 endTime 3
                // meeting time = start 1 end 2 then >> 1 is before 3 (endTime) and 2 (startTime) is not before 2 > result allow
                // meeting time = start 2 end 3 then >> 2 is before 3 (endTime) and 2 (startTime) is before 3 > result deny
                // meeting time = start 3 end 4 then >> 3 is not before 3 (endTime) and 2 (startTime) is before 4 > result allow
                .noneMatch(meeting ->  startTime.isBefore(meeting.getEndTime()) && meeting.getStartTime().isBefore(endTime));
    }

    @Override
    public List<Meeting> getUpcomingMeetingsForPerson(Person person) {
        if (person == null) {
            return new ArrayList<>();
        }
        // to fetch meetings after current time - sorted by start time to display it properly
        return meetings.stream()
                .filter(meeting -> meeting.getStartTime().isAfter(LocalDateTime.now()))
                .filter(meeting -> meeting.getOrganizer().equals(person) || meeting.getAttendees().contains(person))
                .sorted(Comparator.comparing(Meeting::getStartTime))
                .collect(Collectors.toList());
    }

    @Override
    public List<LocalDateTime> suggestAvailableTimeSlots(List<Person> participants, LocalDateTime startDate, LocalDateTime endDate, int maxSuggestions) {
        suggestAvailableTimeSlotsValidation(participants, startDate, endDate);
        List<LocalDateTime> suggestions = new ArrayList<>();
        LocalDateTime current = startDate.withMinute(0).withSecond(0).withNano(0);
        // If start time is not at hr mark, move to NEXT hour
        if (startDate.getMinute() != 0 || startDate.getSecond() != 0 || startDate.getNano() != 0) {
            current = current.plusHours(1);
        }
        while (current.isBefore(endDate) && suggestions.size() < maxSuggestions) {
            if (isTimeSlotAvailable(participants, current)) {
                suggestions.add(current);
            }
            current = current.plusHours(1);
        }
        log.info("Found {} available time slots for {} persons between {} and {}", 
                suggestions.size(), participants.size(), startDate, endDate);
        
        return suggestions;
    }

    @Override
    public List<Meeting> getAllMeetings() {
        return new ArrayList<>(meetings);
    }

    private static void suggestAvailableTimeSlotsValidation(List<Person> participants, LocalDateTime startDate, LocalDateTime endDate) {
        if (participants == null || participants.isEmpty()) {
        throw new IllegalArgumentException("At least one person is required");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }

    @Override
    public Optional<Meeting> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(meetingsById.get(id));
    }

}
