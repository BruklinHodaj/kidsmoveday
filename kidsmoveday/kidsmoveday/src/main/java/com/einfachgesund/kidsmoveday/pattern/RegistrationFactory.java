package com.einfachgesund.kidsmoveday.pattern;

import com.einfachgesund.kidsmoveday.model.Registration;
import com.einfachgesund.kidsmoveday.model.User;

/**
 * Factory Pattern implementation for creating Registration objects.
 *
 * <p>Centralizes and standardizes the creation of Registration instances.
 * Separates object construction from business logic in RegistrationService,
 * making the code easier to maintain and extend.</p>
 *
 * <p>Covers: R9 (Design Patterns - Factory)</p>
 *
 * @author [FirstName LastName]
 * @version 1.0
 */
public class RegistrationFactory {

    /**
     * Enum defining the supported types of registration.
     */
    public enum RegistrationType {
        /** Registered EinfachGesund insurance member. */
        USER,
        /** Walk-in participant without a user account. */
        GUEST,
        /** Added to the waiting list when event is full. */
        WAITLIST
    }

    /**
     * Creates a confirmed Registration for an insured user.
     *
     * @param user         the insured user registering
     * @param kinderAnzahl number of children attending
     * @return a new Registration with status BESTAETIGT
     */
    public static Registration createUserRegistration(User user, int kinderAnzahl) {
        return Registration.builder()
                .user(user)
                .kinderAnzahl(kinderAnzahl)
                .status("BESTAETIGT")
                .build();
    }

    /**
     * Creates a confirmed Registration for a guest participant.
     *
     * @param gastName     full name of the guest
     * @param gastEmail    email address of the guest
     * @param kinderAnzahl number of children attending
     * @return a new Registration with status BESTAETIGT
     */
    public static Registration createGuestRegistration(String gastName,
                                                       String gastEmail,
                                                       int kinderAnzahl) {
        return Registration.builder()
                .gastName(gastName)
                .gastEmail(gastEmail)
                .kinderAnzahl(kinderAnzahl)
                .status("BESTAETIGT")
                .build();
    }

    /**
     * Creates a placeholder Registration for the waiting list.
     *
     * @param gastEmail email of the person on the waiting list
     * @return a new Registration with status WARTELISTE
     */
    public static Registration createWaitlistRegistration(String gastEmail) {
        return Registration.builder()
                .gastEmail(gastEmail)
                .kinderAnzahl(0)
                .status("WARTELISTE")
                .build();
    }

    /**
     * General factory method that dispatches based on RegistrationType.
     *
     * @param type         the type of registration to create
     * @param user         the user (required for USER type, null otherwise)
     * @param gastName     guest name (required for GUEST type)
     * @param gastEmail    guest or waitlist email
     * @param kinderAnzahl number of children
     * @return the appropriate Registration object
     */
    public static Registration create(RegistrationType type,
                                      User user,
                                      String gastName,
                                      String gastEmail,
                                      int kinderAnzahl) {
        return switch (type) {
            case USER     -> createUserRegistration(user, kinderAnzahl);
            case GUEST    -> createGuestRegistration(gastName, gastEmail, kinderAnzahl);
            case WAITLIST -> createWaitlistRegistration(gastEmail);
        };
    }
}