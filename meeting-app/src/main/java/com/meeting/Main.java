package com.meeting;

import com.meeting.entity.Person;
import com.meeting.service.PersonService;
import com.meeting.service.impl.PersonServiceImpl;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Scheduling Application Demo ===\n");

        PersonService personService = new PersonServiceImpl();
        try {
            System.out.println("Creating persons with unique email validation:");
            Person alice = personService.createPerson("Alice Person1", "alice@company.com");
            System.out.println("Created: " + alice);
            // duplicate
            try {
                personService.createPerson("Alice Duplicate", "alice@company.com");
            } catch (IllegalArgumentException e) {
                System.out.println("Duplicate email validation: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Demo failed with error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}