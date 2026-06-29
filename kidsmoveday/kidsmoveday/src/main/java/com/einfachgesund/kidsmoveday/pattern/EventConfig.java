package com.einfachgesund.kidsmoveday.pattern;

/**
 * Singleton Pattern implementation for global event configuration.
 *
 * <p>Ensures only one instance of EventConfig exists throughout
 * the application lifecycle. Holds immutable event details for
 * Kids Move Day 2025.</p>
 *
 * <p>Covers: R9 (Design Patterns - Singleton)</p>
 *
 * @author [FirstName LastName]
 * @version 1.0
 */
public class EventConfig {

    /** The single instance of this class. */
    private static EventConfig instance;

    private final String eventName;
    private final String eventDate;
    private final String eventLocation;
    private final int maxParticipants;

    /**
     * Private constructor — prevents external instantiation.
     * Initializes event configuration values.
     */
    private EventConfig() {
        this.eventName     = "Kids Move Day 2025";
        this.eventDate     = "15. Juni 2025";
        this.eventLocation = "Stadtpark Mitte";
        this.maxParticipants = 500;
    }

    /**
     * Returns the single instance of EventConfig.
     * Thread-safe via synchronized keyword.
     *
     * @return the singleton EventConfig instance
     */
    public static synchronized EventConfig getInstance() {
        if (instance == null) {
            instance = new EventConfig();
        }
        return instance;
    }

    /**
     * Returns the event name.
     * @return event name string
     */
    public String getEventName() { return eventName; }

    /**
     * Returns the event date.
     * @return event date string
     */
    public String getEventDate() { return eventDate; }

    /**
     * Returns the event location.
     * @return event location string
     */
    public String getEventLocation() { return eventLocation; }

    /**
     * Returns the maximum number of participants allowed.
     * @return max participants integer
     */
    public int getMaxParticipants() { return maxParticipants; }

    /**
     * Returns a formatted string summary of the event configuration.
     * @return formatted event info string
     */
    @Override
    public String toString() {
        return eventName + " | " + eventDate + " | "
                + eventLocation + " | Max: " + maxParticipants;
    }
}