package com.meeting.service;

import com.meeting.entity.Meeting;
import com.meeting.entity.Person;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingService {
    Meeting createMeeting(String title, LocalDateTime startTime, Person organizer, List<Person> attendees);

    boolean isTimeSlotAvailable(List<Person> persons, LocalDateTime startTime);

    List<Meeting> getUpcomingMeetingsForPerson(Person person);

    List<LocalDateTime> suggestAvailableTimeSlots(List<Person> participants, LocalDateTime searchStart, LocalDateTime searchEnd, int i);
}
