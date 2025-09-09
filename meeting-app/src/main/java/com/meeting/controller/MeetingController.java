package com.meeting.controller;


import com.meeting.entity.Meeting;
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
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class MeetingController {

    @PostMapping
    public ResponseEntity<Meeting> createMeeting(@Valid @RequestBody Meeting meeting) {
        return null;
    }


}
