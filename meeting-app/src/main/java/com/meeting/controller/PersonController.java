package com.meeting.controller;


import com.meeting.dto.PersonDTO;
import com.meeting.entity.Meeting;
import com.meeting.entity.Person;
import com.meeting.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public ResponseEntity<?> createPerson(@Valid @RequestBody PersonDTO personDTO) {
        try {
            Person person = personService.createPerson(personDTO.getName(), personDTO.getEmail());
            PersonDTO responseDTO = PersonDTO.builder()
                    .uuid(person.getUuid())
                    .name(person.getName())
                    .email(person.getEmail())
                    .build();
            log.info("Created person: {}", person.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (IllegalArgumentException e) {
            log.error("Failed to create person: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



}
