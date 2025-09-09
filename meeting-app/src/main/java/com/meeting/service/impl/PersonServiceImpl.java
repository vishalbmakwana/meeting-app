package com.meeting.service.impl;

import com.meeting.entity.Person;
import com.meeting.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonServiceImpl implements PersonService {

    // In-memory storage for persons (email -> person mapping for uniqueness)
    private final Map<String, Person> personsByEmail = new ConcurrentHashMap<>();
    private final Map<String, Person> personsById = new ConcurrentHashMap<>();

}
