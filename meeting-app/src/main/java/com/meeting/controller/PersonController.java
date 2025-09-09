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
import java.util.Optional;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PersonController {

    private final PersonService personService;

    @PostMapping("/add")
    public ResponseEntity<?> createPerson(@Valid @RequestBody PersonDTO personDTO) {
        try {
            Person person = personService.createPerson(personDTO.getName(), personDTO.getEmail());
            log.info("Created person: {}", person.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(getPersonDTO(person));
        } catch (IllegalArgumentException e) {
            log.error("Failed to create person: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Person>> getAllPersons() {
        List<Person> persons = personService.getAllPersons();
        log.info("Retrieved {} persons", persons.size());
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPersonById(@PathVariable String id) {
        Optional<Person> person = personService.findById(id);
        if (person.isPresent()) {
            return ResponseEntity.ok(person.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
	
    private static PersonDTO getPersonDTO(Person person) {
        PersonDTO responseDTO = PersonDTO.builder()
                .uuid(person.getUuid())
                .name(person.getName())
                .email(person.getEmail())
                .build();
        return responseDTO;
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getPersonByEmail(@PathVariable String email) {
        Optional<Person> person = personService.findByEmail(email);
        if (person.isPresent()) {
            return ResponseEntity.ok(person.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
