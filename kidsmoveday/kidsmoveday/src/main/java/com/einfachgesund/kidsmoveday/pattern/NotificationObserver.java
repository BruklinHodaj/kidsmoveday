package com.einfachgesund.kidsmoveday.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer Pattern implementation for event-driven notifications.
 *
 * <p>Allows multiple listeners to be notified when registration
 * events occur (e.g. new registration, guest sign-up).
 * Contains the EventListener interface, concrete implementations,
 * and the EventPublisher subject class.</p>
 *
 * <p>Covers: R9 (Design Patterns - Observer)</p>
 *
 * @author [FirstName LastName]
 * @version 1.0
 */
public class NotificationObserver {

    /**
     * Observer interface to be implemented by all event listeners.
     */
    public interface EventListener {
        /**
         * Called when an event is published.
         * @param eventType type of event (e.g. REGISTRATION)
         * @param message   descriptive message about the event
         */
        void onEvent(String eventType, String message);
    }

    /**
     * Concrete observer that logs events to the console.
     */
    public static class ConsoleLogger implements EventListener {

        /**
         * Prints the event to standard output.
         * @param eventType type of event
         * @param message   event message
         */
        @Override
        public void onEvent(String eventType, String message) {
            System.out.println("[LOG] " + eventType + ": " + message);
        }
    }

    /**
     * Concrete observer that simulates sending email notifications.
     */
    public static class EmailNotifier implements EventListener {

        /** The recipient email address. */
        private final String recipientEmail;

        /**
         * Constructs an EmailNotifier for a specific recipient.
         * @param email the recipient's email address
         */
        public EmailNotifier(String email) {
            this.recipientEmail = email;
        }

        /**
         * Simulates sending an email notification.
         * @param eventType type of event
         * @param message   event message
         */
        @Override
        public void onEvent(String eventType, String message) {
            System.out.println("[EMAIL -> " + recipientEmail + "] "
                    + eventType + ": " + message);
        }
    }

    /**
     * Subject class that manages subscribers and publishes events.
     */
    public static class EventPublisher {

        /** List of registered event listeners. */
        private final List<EventListener> listeners = new ArrayList<>();

        /**
         * Registers a new listener to receive events.
         * @param listener the listener to add
         */
        public void subscribe(EventListener listener) {
            listeners.add(listener);
        }

        /**
         * Removes a listener from the notification list.
         * @param listener the listener to remove
         */
        public void unsubscribe(EventListener listener) {
            listeners.remove(listener);
        }

        /**
         * Publishes an event to all registered listeners.
         * @param eventType type of event
         * @param message   descriptive message
         */
        public void publish(String eventType, String message) {
            for (EventListener listener : listeners) {
                listener.onEvent(eventType, message);
            }
        }
    }
}