package com.meeting.controller;


import com.meeting.entity.Meeting;
import com.meeting.service.MeetingService;
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
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class MeetingController {

    private final MeetingService meetingService;
    private final PersonService personService;

    @PostMapping
    public ResponseEntity<Meeting> createMeeting(@Valid @RequestBody Meeting meeting) {
        return null;
    }

    @GetMapping
    public ResponseEntity<List<Meeting>> getAllMeetings() {
        List<Meeting> meetings = meetingService.getAllMeetings();
        log.info("Retrieved {} meetings", meetings.size());
        return ResponseEntity.ok(meetings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMeetingById(@PathVariable String id) {
        Optional<Meeting> meeting = meetingService.findById(id);
        if (meeting.isPresent()) {
            return ResponseEntity.ok(meeting.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
