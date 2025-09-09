package com.meeting.controller;


import com.meeting.dto.PersonDTO;
import com.meeting.entity.Meeting;
import com.meeting.entity.Person;
import com.meeting.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PersonController {

    private final PersonService personService;

    @PostMapping("/add")
    public ResponseEntity<Person> createPerson(@Valid @RequestBody PersonDTO personDTO) {
//        this.personService.createPerson(personDTO);
        return null;
    }




}
