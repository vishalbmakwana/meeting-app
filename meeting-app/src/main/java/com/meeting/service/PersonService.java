package com.meeting.service;

import com.meeting.entity.Person;

public interface PersonService {
    Person createPerson(String name, String email);

    boolean emailExists(String email);
}
