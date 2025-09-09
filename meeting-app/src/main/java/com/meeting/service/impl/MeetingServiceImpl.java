package com.meeting.service.impl;

import com.meeting.entity.Meeting;
import com.meeting.entity.Person;
import com.meeting.service.MeetingService;
import com.meeting.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        return null;
    }
}
