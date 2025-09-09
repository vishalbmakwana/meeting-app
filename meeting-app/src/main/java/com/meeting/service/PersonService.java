package com.meeting.service;

import com.meeting.entity.Person;

import java.util.List;

public interface PersonService {
    Person createPerson(String name, String email);

    boolean emailExists(String email);

    List<Person> getAllPersons();
}
