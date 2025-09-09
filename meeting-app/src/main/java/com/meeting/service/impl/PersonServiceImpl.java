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

    @Override
    public Person createPerson(String name, String email) {
        log.info("Creating person with name: {} and email: {}", name, email);
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (emailExists(email)) {
            throw new IllegalArgumentException("Person with email " + email + " already exists");
        }
        Person person = new Person(name.trim(), email.trim().toLowerCase());
        personsByEmail.put(person.getEmail(), person);
        personsById.put(person.getUuid(), person);
        log.info("Successfully created person: {}", person);
        return person;
    }

    @Override
    public Optional<Person> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(personsByEmail.get(email.trim().toLowerCase()));
    }

    @Override
    public boolean emailExists(String email) {
        if (email == null) {
            return false;
        }
        return personsByEmail.containsKey(email.trim().toLowerCase());
    }

    @Override
    public List<Person> getAllPersons() {
        return new ArrayList<>(personsByEmail.values());
    }
    
}
