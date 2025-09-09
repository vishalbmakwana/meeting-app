package com.meeting.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class MeetingDTO {

    private String uuid;

    @NotBlank(message = "Name is required")
    private String title;
    
    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;

    @NotNull(message = "Organizer is required")
    private PersonDTO organizer;

    @NotEmpty(message = "At least one attendee is required")
    private List<@NotNull PersonDTO> attendees;

}
