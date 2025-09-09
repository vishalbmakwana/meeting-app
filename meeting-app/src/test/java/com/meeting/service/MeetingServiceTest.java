package com.meeting.service;

import com.meeting.entity.Meeting;
import com.meeting.entity.Person;
import com.meeting.service.impl.MeetingServiceImpl;
import com.meeting.service.impl.PersonServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MeetingServiceTest {

    private MeetingService meetingService;
    private PersonService personService;
    private Person organizer;
    private Person attendee1;
    private Person attendee2;

    @BeforeEach
    void setUp() {
        personService = new PersonServiceImpl();
        meetingService = new MeetingServiceImpl(personService);
        
        // Create test persons
        organizer = personService.createPerson("John Organizer", "john.organizer@example.com");
        attendee1 = personService.createPerson("Alice Attendee", "alice.attendee@example.com");
        attendee2 = personService.createPerson("Bob Attendee", "bob.attendee@example.com");
    }

    @Test
    void testCreateMeeting_ValidInput_ShouldCreateMeeting() {
        // Given
        String title = "Team Meeting";
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 15, 10, 0);
        List<Person> attendees = Arrays.asList(attendee1, attendee2);

        // When
        Meeting meeting = meetingService.createMeeting(title, startTime, organizer, attendees);

        // Then
        assertNotNull(meeting);
        assertEquals(title, meeting.getTitle());
        assertEquals(startTime, meeting.getStartTime());
        assertEquals(startTime.plusHours(1), meeting.getEndTime());
        assertEquals(organizer, meeting.getOrganizer());
        assertEquals(attendees, meeting.getAttendees());
        assertNotNull(meeting.getUuid());
    }

    @Test
    void testCreateMeeting_WithWhitespace_ShouldTrimTitle() {
        // Given
        String title = "  Team Meeting  ";
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 15, 10, 0);
        List<Person> attendees = Arrays.asList(attendee1);

        // When
        Meeting meeting = meetingService.createMeeting(title, startTime, organizer, attendees);

        // Then
        assertEquals("Team Meeting", meeting.getTitle());
    }

    @Test
    void testCreateMeeting_NonExistentOrganizer_ShouldThrowException() {
        // Given
        Person nonExistentOrganizer = new Person("Non Existent", "nonexistent@example.com");
        String title = "Team Meeting";
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 15, 10, 0);
        List<Person> attendees = Arrays.asList(attendee1);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> meetingService.createMeeting(title, startTime, nonExistentOrganizer, attendees)
        );
        assertEquals("Person with email nonexistent@example.com does not exist in the system", exception.getMessage());
    }

    @Test
    void testCreateMeeting_NonExistentAttendee_ShouldThrowException() {
        // Given
        Person nonExistentAttendee = new Person("Non Existent", "nonexistent@example.com");
        String title = "Team Meeting";
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 15, 10, 0);
        List<Person> attendees = Arrays.asList(attendee1, nonExistentAttendee);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> meetingService.createMeeting(title, startTime, organizer, attendees)
        );
        assertEquals("Person with email nonexistent@example.com does not exist in the system", exception.getMessage());
    }

    @Test
    void testCreateMeeting_SchedulingConflict_ShouldThrowException() {
        // Given
        String title1 = "First Meeting";
        String title2 = "Second Meeting";
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 15, 10, 0);
        List<Person> attendees = Arrays.asList(attendee1);

        // Create first meeting
        meetingService.createMeeting(title1, startTime, organizer, attendees);

        // When & Then - Try to create overlapping meeting
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> meetingService.createMeeting(title2, startTime, organizer, attendees)
        );
        assertEquals("One or more participants have a scheduling conflict at " + startTime, exception.getMessage());
    }

    @Test
    void testCreateMeeting_InvalidStartTime_ShouldThrowException() {
        // Given
        String title = "Team Meeting";
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 15, 10, 30); // Not at hour mark
        List<Person> attendees = Arrays.asList(attendee1);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> meetingService.createMeeting(title, startTime, organizer, attendees)
        );
        assertEquals("Meeting can only start at the hour mark (e.g., 10:00, 11:00)", exception.getMessage());
    }

    @Test
    void testIsTimeSlotAvailable_NoConflicts_ShouldReturnTrue() {
        // Given
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 15, 10, 0);
        List<Person> participants = Arrays.asList(organizer, attendee1);

        // When
        boolean available = meetingService.isTimeSlotAvailable(participants, startTime);

        // Then
        assertTrue(available);
    }

    @Test
    void testIsTimeSlotAvailable_WithConflict_ShouldReturnFalse() {
        // Given
        LocalDateTime startTime = LocalDateTime.of(2024, 12, 15, 10, 0);
        List<Person> attendees = Arrays.asList(attendee1);
        List<Person> participants = Arrays.asList(organizer, attendee1);

        // Create a meeting that conflicts
        meetingService.createMeeting("Existing Meeting", startTime, organizer, attendees);

        // When
        boolean available = meetingService.isTimeSlotAvailable(participants, startTime);

        // Then
        assertFalse(available);
    }

    @Test
    void testIsTimeSlotAvailable_NullParameters_ShouldReturnFalse() {
        // When & Then
        assertFalse(meetingService.isTimeSlotAvailable(null, LocalDateTime.now()));
        assertFalse(meetingService.isTimeSlotAvailable(Arrays.asList(organizer), null));
        assertFalse(meetingService.isTimeSlotAvailable(Arrays.asList(), LocalDateTime.now()));
    }

    @Test
    void testIsTimeSlotAvailable_AdjacentMeetings_ShouldReturnTrue() {
        // Given
        LocalDateTime firstMeetingTime = LocalDateTime.of(2024, 12, 15, 10, 0);
        LocalDateTime secondMeetingTime = LocalDateTime.of(2024, 12, 15, 11, 0);
        List<Person> attendees = Arrays.asList(attendee1);

        // Create first meeting (10:00-11:00)
        meetingService.createMeeting("First Meeting", firstMeetingTime, organizer, attendees);

        // When - Check if 11:00-12:00 is available
        boolean available = meetingService.isTimeSlotAvailable(Arrays.asList(organizer), secondMeetingTime);

        // Then
        assertTrue(available);
    }

    @Test
    void testGetUpcomingMeetingsForPerson_AsOrganizer_ShouldReturnMeetings() {
        // Given
        LocalDateTime futureTime1 = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime futureTime2 = LocalDateTime.now().plusDays(2).withHour(14).withMinute(0).withSecond(0).withNano(0);
        
        Meeting meeting1 = meetingService.createMeeting("Meeting 1", futureTime1, organizer, Arrays.asList(attendee1));
        Meeting meeting2 = meetingService.createMeeting("Meeting 2", futureTime2, organizer, Arrays.asList(attendee2));

        // When
        List<Meeting> upcomingMeetings = meetingService.getUpcomingMeetingsForPerson(organizer);

        // Then
        assertEquals(2, upcomingMeetings.size());
        assertTrue(upcomingMeetings.contains(meeting1));
        assertTrue(upcomingMeetings.contains(meeting2));
        // Check if sorted by start time
        assertEquals(meeting1, upcomingMeetings.get(0));
        assertEquals(meeting2, upcomingMeetings.get(1));
    }

    @Test
    void testGetUpcomingMeetingsForPerson_AsAttendee_ShouldReturnMeetings() {
        // Given
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        Meeting meeting = meetingService.createMeeting("Meeting", futureTime, organizer, Arrays.asList(attendee1));

        // When
        List<Meeting> upcomingMeetings = meetingService.getUpcomingMeetingsForPerson(attendee1);

        // Then
        assertEquals(1, upcomingMeetings.size());
        assertTrue(upcomingMeetings.contains(meeting));
    }

    @Test
    void testGetUpcomingMeetingsForPerson_NullPerson_ShouldReturnEmptyList() {
        // When
        List<Meeting> upcomingMeetings = meetingService.getUpcomingMeetingsForPerson(null);

        // Then
        assertNotNull(upcomingMeetings);
        assertTrue(upcomingMeetings.isEmpty());
    }

    @Test
    void testGetUpcomingMeetingsForPerson_PastMeetings_ShouldNotInclude() {
        // Given
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        meetingService.createMeeting("Past Meeting", pastTime, organizer, Arrays.asList(attendee1));

        // When
        List<Meeting> upcomingMeetings = meetingService.getUpcomingMeetingsForPerson(organizer);

        // Then
        assertTrue(upcomingMeetings.isEmpty());
    }

    @Test
    void testSuggestAvailableTimeSlots_NoConflicts_ShouldReturnSlots() {
        // Given
        LocalDateTime searchStart = LocalDateTime.of(2024, 12, 15, 9, 0);
        LocalDateTime searchEnd = LocalDateTime.of(2024, 12, 15, 13, 0);
        List<Person> participants = Arrays.asList(organizer, attendee1);
        int maxSuggestions = 3;

        // When
        List<LocalDateTime> suggestions = meetingService.suggestAvailableTimeSlots(
            participants, searchStart, searchEnd, maxSuggestions);

        // Then
        assertEquals(3, suggestions.size());
        assertEquals(LocalDateTime.of(2024, 12, 15, 9, 0), suggestions.get(0));
        assertEquals(LocalDateTime.of(2024, 12, 15, 10, 0), suggestions.get(1));
        assertEquals(LocalDateTime.of(2024, 12, 15, 11, 0), suggestions.get(2));
    }

    @Test
    void testSuggestAvailableTimeSlots_WithConflicts_ShouldSkipConflictedSlots() {
        // Given
        LocalDateTime searchStart = LocalDateTime.of(2024, 12, 15, 9, 0);
        LocalDateTime searchEnd = LocalDateTime.of(2024, 12, 15, 13, 0);
        LocalDateTime conflictTime = LocalDateTime.of(2024, 12, 15, 10, 0);
        List<Person> participants = Arrays.asList(organizer, attendee1);
        
        // Create a conflicting meeting
        meetingService.createMeeting("Conflict Meeting", conflictTime, organizer, Arrays.asList(attendee1));

        // When
        List<LocalDateTime> suggestions = meetingService.suggestAvailableTimeSlots(
            participants, searchStart, searchEnd, 3);

        // Then
        assertEquals(3, suggestions.size());
        assertEquals(LocalDateTime.of(2024, 12, 15, 9, 0), suggestions.get(0));
        assertEquals(LocalDateTime.of(2024, 12, 15, 11, 0), suggestions.get(1));
        assertEquals(LocalDateTime.of(2024, 12, 15, 12, 0), suggestions.get(2));
        assertFalse(suggestions.contains(conflictTime));
    }

    @Test
    void testSuggestAvailableTimeSlots_NonHourStartTime_ShouldStartFromNextHour() {
        // Given
        LocalDateTime searchStart = LocalDateTime.of(2024, 12, 15, 9, 30);
        LocalDateTime searchEnd = LocalDateTime.of(2024, 12, 15, 12, 0);
        List<Person> participants = Arrays.asList(organizer);

        // When
        List<LocalDateTime> suggestions = meetingService.suggestAvailableTimeSlots(
            participants, searchStart, searchEnd, 3);

        // Then
        assertEquals(2, suggestions.size());
        assertEquals(LocalDateTime.of(2024, 12, 15, 10, 0), suggestions.get(0));
        assertEquals(LocalDateTime.of(2024, 12, 15, 11, 0), suggestions.get(1));
    }

    @Test
    void testSuggestAvailableTimeSlots_InvalidParameters_ShouldThrowException() {
        LocalDateTime searchStart = LocalDateTime.of(2024, 12, 15, 9, 0);
        LocalDateTime searchEnd = LocalDateTime.of(2024, 12, 15, 13, 0);

        // Test null participants
        assertThrows(IllegalArgumentException.class, () ->
            meetingService.suggestAvailableTimeSlots(null, searchStart, searchEnd, 3));

        // Test empty participants
        assertThrows(IllegalArgumentException.class, () ->
            meetingService.suggestAvailableTimeSlots(Arrays.asList(), searchStart, searchEnd, 3));

        // Test null dates
        assertThrows(IllegalArgumentException.class, () ->
            meetingService.suggestAvailableTimeSlots(Arrays.asList(organizer), null, searchEnd, 3));

        assertThrows(IllegalArgumentException.class, () ->
            meetingService.suggestAvailableTimeSlots(Arrays.asList(organizer), searchStart, null, 3));

        // Test start after end
        assertThrows(IllegalArgumentException.class, () ->
            meetingService.suggestAvailableTimeSlots(Arrays.asList(organizer), searchEnd, searchStart, 3));
    }

    @Test
    void testGetAllMeetings_EmptyList_ShouldReturnEmptyList() {
        // When
        List<Meeting> meetings = meetingService.getAllMeetings();

        // Then
        assertNotNull(meetings);
        assertTrue(meetings.isEmpty());
    }

    @Test
    void testGetAllMeetings_WithMeetings_ShouldReturnAllMeetings() {
        // Given
        LocalDateTime time1 = LocalDateTime.of(2024, 12, 15, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 12, 15, 14, 0);
        
        Meeting meeting1 = meetingService.createMeeting("Meeting 1", time1, organizer, Arrays.asList(attendee1));
        Meeting meeting2 = meetingService.createMeeting("Meeting 2", time2, attendee2, Arrays.asList(attendee1));

        // When
        List<Meeting> meetings = meetingService.getAllMeetings();

        // Then
        assertEquals(2, meetings.size());
        assertTrue(meetings.contains(meeting1));
        assertTrue(meetings.contains(meeting2));
    }

    @Test
    void testGetAllMeetings_ShouldReturnNewListInstance() {
        // Given
        LocalDateTime time = LocalDateTime.of(2024, 12, 15, 10, 0);
        meetingService.createMeeting("Meeting", time, organizer, Arrays.asList(attendee1));

        // When
        List<Meeting> meetings1 = meetingService.getAllMeetings();
        List<Meeting> meetings2 = meetingService.getAllMeetings();

        // Then
        assertNotSame(meetings1, meetings2);
        assertEquals(meetings1.size(), meetings2.size());
    }

    @Test
    void testFindById_ExistingId_ShouldReturnMeeting() {
        // Given
        LocalDateTime time = LocalDateTime.of(2024, 12, 15, 10, 0);
        Meeting meeting = meetingService.createMeeting("Meeting", time, organizer, Arrays.asList(attendee1));

        // When
        Optional<Meeting> foundMeeting = meetingService.findById(meeting.getUuid());

        // Then
        assertTrue(foundMeeting.isPresent());
        assertEquals(meeting.getUuid(), foundMeeting.get().getUuid());
        assertEquals(meeting.getTitle(), foundMeeting.get().getTitle());
    }

    @Test
    void testFindById_NonExistingId_ShouldReturnEmpty() {
        // Given
        String nonExistentId = "non-existent-uuid";

        // When
        Optional<Meeting> foundMeeting = meetingService.findById(nonExistentId);

        // Then
        assertFalse(foundMeeting.isPresent());
    }

    @Test
    void testFindById_NullId_ShouldReturnEmpty() {
        // When
        Optional<Meeting> foundMeeting = meetingService.findById(null);

        // Then
        assertFalse(foundMeeting.isPresent());
    }
}
