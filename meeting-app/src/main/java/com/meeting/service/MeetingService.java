package com.meeting.service;

import com.meeting.entity.Meeting;
import com.meeting.entity.Person;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingService {
    Meeting createMeeting(String title, LocalDateTime startTime, Person organizer, List<Person> attendees);
}
