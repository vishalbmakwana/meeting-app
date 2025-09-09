package com.meeting.service;

import com.meeting.entity.Meeting;
import com.meeting.entity.Person;
import com.meeting.service.impl.MeetingServiceImpl;
import com.meeting.service.impl.PersonServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MeetingServiceTest {

    private MeetingService meetingService;
    private PersonService personService;
    private Person organizer;
    private Person attendee1;
    private Person attendee2;


}
