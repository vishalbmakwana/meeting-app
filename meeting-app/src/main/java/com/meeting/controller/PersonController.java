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






}
