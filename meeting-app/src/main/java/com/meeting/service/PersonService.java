package com.meeting.service;

import com.meeting.entity.Person;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    Person createPerson(String name, String email);

    boolean emailExists(String email);

    List<Person> getAllPersons();

    Optional<Person> findByEmail(String email);
}
