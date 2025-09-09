package com.meeting.entity;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Meeting {

    private String uuid;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;

    @NotNull(message = "Organizer is required")
    private Person organizer;

    @NotEmpty(message = "At least one attendee is required")
    private List<@NotNull Person> attendees;

    public Meeting(String title, LocalDateTime startTime, Person organizer, List<Person> attendees) {
        this.uuid = UUID.randomUUID().toString();
        this.title = title;
        this.startTime = validateAndSetStartTime(startTime);
        this.endTime = startTime.plusHours(1);
        this.organizer = organizer;
        this.attendees = attendees;
    }
    private LocalDateTime validateAndSetStartTime(LocalDateTime startTime) {
        if (startTime.getMinute() != 0 || startTime.getSecond() != 0 || startTime.getNano() != 0) {
            throw new IllegalArgumentException("Meeting can only start at the hour mark (e.g., 10:00, 11:00)");
        }
        return startTime;
    }
    public boolean involvesAnyPerson(List<Person> persons) {
        return persons.stream().anyMatch(person -> 
            this.organizer.equals(person) || this.attendees.contains(person));
    }
}
