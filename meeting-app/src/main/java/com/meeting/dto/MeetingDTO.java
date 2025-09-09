package com.meeting.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MeetingDTO {

    private String uuid;

    @NotBlank(message = "Title is required")
    private String title;
    
    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;

    @NotBlank(message = "Organizer email is required")
    private String organizerEmail;

    @NotEmpty(message = "At least one attendee email is required")
    private List<@NotBlank String> attendeeEmails;

}
