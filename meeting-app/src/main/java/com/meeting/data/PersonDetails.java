package com.meeting.data;

import com.meeting.entity.Person;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersonDetails {

    // UNIQUE BASED ON KEY WHERE KEY = EMAIL ID
    private final Map<String, Person> persons = new ConcurrentHashMap<>();

}
