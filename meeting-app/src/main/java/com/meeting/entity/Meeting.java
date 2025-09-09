package com.meeting.entity;

import java.time.LocalDateTime;
import java.util.List;

public class Meeting {

    private String title;

    private LocalDateTime stateTime;

    private LocalDateTime endTime;

    Person organizer;

    List<Person> attendees;

}
