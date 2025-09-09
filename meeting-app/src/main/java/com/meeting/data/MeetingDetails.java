package com.meeting.data;

import com.meeting.entity.Meeting;
import com.meeting.entity.Person;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MeetingDetails {

    private final Map<UUID, Meeting> meetings = new ConcurrentHashMap<>();


}
