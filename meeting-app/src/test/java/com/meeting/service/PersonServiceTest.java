package com.meeting.service;

import com.meeting.entity.Person;
import com.meeting.service.impl.PersonServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PersonServiceTest {

    private PersonService personService;

    @BeforeEach
    void setUp() {
        personService = new PersonServiceImpl();
    }

    @Test
    void testCreatePerson_ValidInput_ShouldCreatePerson() {
        // Given
        String name = "John Doe";
        String email = "john.doe@example.com";

        // When
        Person createdPerson = personService.createPerson(name, email);

        // Then
        assertNotNull(createdPerson);
        assertEquals(name, createdPerson.getName());
        assertEquals(email.toLowerCase(), createdPerson.getEmail());
        assertNotNull(createdPerson.getUuid());
    }

    @Test
    void testCreatePerson_WithWhitespace_ShouldTrimAndCreatePerson() {
        // Given
        String name = "  Jane Smith  ";
        String email = "  JANE.SMITH@EXAMPLE.COM  ";

        // When
        Person createdPerson = personService.createPerson(name, email);

        // Then
        assertNotNull(createdPerson);
        assertEquals("Jane Smith", createdPerson.getName());
        assertEquals("jane.smith@example.com", createdPerson.getEmail());
    }

    @Test
    void testCreatePerson_NullName_ShouldThrowException() {
        // Given
        String email = "test@example.com";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> personService.createPerson(null, email)
        );
        assertEquals("Name cannot be null or empty", exception.getMessage());
    }

    @Test
    void testCreatePerson_EmptyName_ShouldThrowException() {
        // Given
        String name = "   ";
        String email = "test@example.com";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> personService.createPerson(name, email)
        );
        assertEquals("Name cannot be null or empty", exception.getMessage());
    }

    @Test
    void testCreatePerson_NullEmail_ShouldThrowException() {
        // Given
        String name = "John Doe";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> personService.createPerson(name, null)
        );
        assertEquals("Email cannot be null or empty", exception.getMessage());
    }

    @Test
    void testCreatePerson_EmptyEmail_ShouldThrowException() {
        // Given
        String name = "John Doe";
        String email = "   ";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> personService.createPerson(name, email)
        );
        assertEquals("Email cannot be null or empty", exception.getMessage());
    }

    @Test
    void testCreatePerson_DuplicateEmail_ShouldThrowException() {
        // Given
        String name1 = "John Doe";
        String name2 = "Jane Smith";
        String email = "duplicate@example.com";

        // When
        personService.createPerson(name1, email);

        // Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> personService.createPerson(name2, email)
        );
        assertEquals("Person with email " + email + " already exists", exception.getMessage());
    }

    @Test
    void testEmailExists_ExistingEmail_ShouldReturnTrue() {
        // Given
        String name = "John Doe";
        String email = "john.doe@example.com";
        personService.createPerson(name, email);

        // When
        boolean exists = personService.emailExists(email);

        // Then
        assertTrue(exists);
    }

    @Test
    void testEmailExists_NonExistingEmail_ShouldReturnFalse() {
        // Given
        String email = "nonexistent@example.com";

        // When
        boolean exists = personService.emailExists(email);

        // Then
        assertFalse(exists);
    }

    @Test
    void testEmailExists_NullEmail_ShouldReturnFalse() {
        // When
        boolean exists = personService.emailExists(null);

        // Then
        assertFalse(exists);
    }

    @Test
    void testEmailExists_CaseInsensitive_ShouldReturnTrue() {
        // Given
        String name = "John Doe";
        String email = "john.doe@example.com";
        personService.createPerson(name, email);

        // When
        boolean exists = personService.emailExists("JOHN.DOE@EXAMPLE.COM");

        // Then
        assertTrue(exists);
    }

    @Test
    void testFindByEmail_ExistingEmail_ShouldReturnPerson() {
        // Given
        String name = "John Doe";
        String email = "john.doe@example.com";
        Person createdPerson = personService.createPerson(name, email);

        // When
        Optional<Person> foundPerson = personService.findByEmail(email);

        // Then
        assertTrue(foundPerson.isPresent());
        assertEquals(createdPerson.getUuid(), foundPerson.get().getUuid());
        assertEquals(name, foundPerson.get().getName());
        assertEquals(email.toLowerCase(), foundPerson.get().getEmail());
    }

    @Test
    void testFindByEmail_NonExistingEmail_ShouldReturnEmpty() {
        // Given
        String email = "nonexistent@example.com";

        // When
        Optional<Person> foundPerson = personService.findByEmail(email);

        // Then
        assertFalse(foundPerson.isPresent());
    }

    @Test
    void testFindByEmail_NullEmail_ShouldReturnEmpty() {
        // When
        Optional<Person> foundPerson = personService.findByEmail(null);

        // Then
        assertFalse(foundPerson.isPresent());
    }

    @Test
    void testFindByEmail_CaseInsensitive_ShouldReturnPerson() {
        // Given
        String name = "John Doe";
        String email = "john.doe@example.com";
        Person createdPerson = personService.createPerson(name, email);

        // When
        Optional<Person> foundPerson = personService.findByEmail("JOHN.DOE@EXAMPLE.COM");

        // Then
        assertTrue(foundPerson.isPresent());
        assertEquals(createdPerson.getUuid(), foundPerson.get().getUuid());
    }

    @Test
    void testFindById_ExistingId_ShouldReturnPerson() {
        // Given
        String name = "John Doe";
        String email = "john.doe@example.com";
        Person createdPerson = personService.createPerson(name, email);

        // When
        Optional<Person> foundPerson = personService.findById(createdPerson.getUuid());

        // Then
        assertTrue(foundPerson.isPresent());
        assertEquals(createdPerson.getUuid(), foundPerson.get().getUuid());
        assertEquals(name, foundPerson.get().getName());
        assertEquals(email.toLowerCase(), foundPerson.get().getEmail());
    }

    @Test
    void testFindById_NonExistingId_ShouldReturnEmpty() {
        // Given
        String nonExistentId = "non-existent-uuid";

        // When
        Optional<Person> foundPerson = personService.findById(nonExistentId);

        // Then
        assertFalse(foundPerson.isPresent());
    }

    @Test
    void testFindById_NullId_ShouldReturnEmpty() {
        // When
        Optional<Person> foundPerson = personService.findById(null);

        // Then
        assertFalse(foundPerson.isPresent());
    }

    @Test
    void testGetAllPersons_EmptyList_ShouldReturnEmptyList() {
        // When
        List<Person> persons = personService.getAllPersons();

        // Then
        assertNotNull(persons);
        assertTrue(persons.isEmpty());
    }

    @Test
    void testGetAllPersons_WithPersons_ShouldReturnAllPersons() {
        // Given
        Person person1 = personService.createPerson("John Doe", "john@example.com");
        Person person2 = personService.createPerson("Jane Smith", "jane@example.com");
        Person person3 = personService.createPerson("Bob Johnson", "bob@example.com");

        // When
        List<Person> persons = personService.getAllPersons();

        // Then
        assertNotNull(persons);
        assertEquals(3, persons.size());
        assertTrue(persons.contains(person1));
        assertTrue(persons.contains(person2));
        assertTrue(persons.contains(person3));
    }

    @Test
    void testGetAllPersons_ShouldReturnNewListInstance() {
        // Given
        personService.createPerson("John Doe", "john@example.com");

        // When
        List<Person> persons1 = personService.getAllPersons();
        List<Person> persons2 = personService.getAllPersons();

        // Then
        assertNotSame(persons1, persons2);
        assertEquals(persons1.size(), persons2.size());
    }
}
