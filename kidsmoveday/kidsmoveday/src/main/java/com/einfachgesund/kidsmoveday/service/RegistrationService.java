package com.einfachgesund.kidsmoveday.service;

import com.einfachgesund.kidsmoveday.pattern.NotificationObserver;
import com.einfachgesund.kidsmoveday.pattern.RegistrationFactory;
import com.einfachgesund.kidsmoveday.pattern.EventConfig;
import com.einfachgesund.kidsmoveday.exception.RegistrationException;
import com.einfachgesund.kidsmoveday.exception.UserNotFoundException;
import com.einfachgesund.kidsmoveday.model.Registration;
import com.einfachgesund.kidsmoveday.model.User;
import com.einfachgesund.kidsmoveday.repository.RegistrationRepository;
import com.einfachgesund.kidsmoveday.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    /**R5 */
    private final ExecutorService executorService =
            Executors.newFixedThreadPool(3);

    /**
     * R1 - ArrayList: in-memory waiting list of email addresses
     * for participants when the event reaches full capacity.
     */
    private final List<String> waitingList = new ArrayList<>();

    /**
     * R1 - HashMap: tracks registration count per status
     * (BESTAETIGT, STORNIERT) for quick in-memory reporting.
     */
    private final Map<String, Integer> statusCount = new HashMap<>();

    /**R9 - Observer Pattern: publisher that notifies all subscribed listeners when a registration event occurs.*/
    private final NotificationObserver.EventPublisher publisher;

    private static final int MAX_PARTICIPANTS = 500;

    public RegistrationService(RegistrationRepository registrationRepository,
                               UserRepository userRepository) {
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;

        // R9 - Observer: setup publisher with console logger
        this.publisher = new NotificationObserver.EventPublisher();
        publisher.subscribe(new NotificationObserver.ConsoleLogger());

        // R9 - Singleton: log event configuration at startup
        System.out.println("[CONFIG] " + EventConfig.getInstance());
    }

    public Registration registerUser(Long userId, int kinderAnzahl) {
        Integer total = registrationRepository.sumKinderAnzahl();
        if (total != null && total + kinderAnzahl > MAX_PARTICIPANTS) {
            throw new RegistrationException(
                    "Event ist voll! Maximale Teilnehmerzahl erreicht.",
                    "EVENT_FULL"
            );
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (registrationRepository.existsByUserIdAndStatus(
                userId, "BESTAETIGT")) {
            throw new RegistrationException(
                    "Benutzer ist bereits registriert.",
                    "ALREADY_REGISTERED"
            );
        }

        // R9 - Factory Pattern: centralized object creation
        Registration reg = RegistrationFactory
                .createUserRegistration(user, kinderAnzahl);
        Registration saved = registrationRepository.save(reg);

        statusCount.merge("BESTAETIGT", 1, Integer::sum); // R1 HashMap

        // R9 - Observer: notify all listeners
        publisher.publish("REGISTRATION",
                "New registration for: " + user.getName());

        // R5 - Concurrency: async email confirmation
        sendConfirmationEmailAsync(user.getEmail(), user.getName());

        return saved;
    }

    public Registration registerGuest(String gastName,
                                      String gastEmail,
                                      int kinderAnzahl) {
        if (gastName == null || gastName.isBlank()) {
            throw new RegistrationException(
                    "Gastname darf nicht leer sein.", "INVALID_GUEST");
        }
        Integer total = registrationRepository.sumKinderAnzahl();
        if (total != null && total + kinderAnzahl > MAX_PARTICIPANTS) {
            throw new RegistrationException("Event ist voll!", "EVENT_FULL");
        }

        // R9 - Factory Pattern
        Registration reg = RegistrationFactory
                .createGuestRegistration(gastName, gastEmail, kinderAnzahl);
        Registration saved = registrationRepository.save(reg);

        statusCount.merge("BESTAETIGT", 1, Integer::sum); // R1 HashMap

        // R9 - Observer
        publisher.publish("GUEST_REGISTRATION",
                "Guest registered: " + gastName);

        // R5 - Concurrency
        sendConfirmationEmailAsync(gastEmail, gastName);

        return saved;
    }

    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    public List<Registration> getRegistrationsByUser(Long userId) {
        return registrationRepository.findByUserId(userId);
    }

    public Registration getRegistrationById(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new RegistrationException(
                        "Registrierung nicht gefunden: " + id, "NOT_FOUND"
                ));
    }


    public void cancelRegistration(Long id) {
        Registration reg = getRegistrationById(id);
        reg.setStatus("STORNIERT");
        registrationRepository.save(reg);
        statusCount.merge("STORNIERT", 1, Integer::sum); // R1 HashMap
    }

    // ── R3 LAMBDA + R4 STREAM ────────────────────────────────

    public Map<String, Object> getStatistiken() {
        List<Registration> all = registrationRepository.findAll();

        long bestaetigte = all.stream()
                .filter(r -> "BESTAETIGT".equals(r.getStatus())) // R4 filter
                .count();

        int totalKinder = all.stream()
                .filter(r -> "BESTAETIGT".equals(r.getStatus()))
                .mapToInt(Registration::getKinderAnzahl) // R4 mapToInt
                .sum();

        Map<String, Long> perStatus = all.stream()
                .collect(Collectors.groupingBy(  // R4 groupingBy
                        Registration::getStatus,
                        Collectors.counting()
                ));

        Optional<Registration> largest = all.stream()
                .max(Comparator.comparingInt(
                        Registration::getKinderAnzahl)); // R3 Comparator

        Map<String, Object> stats = new HashMap<>(); // R1 HashMap
        stats.put("totalRegistrations", all.size());
        stats.put("bestaetigte", bestaetigte);
        stats.put("totalKinder", totalKinder);
        stats.put("perStatus", perStatus);
        stats.put("largestGroup",
                largest.map(Registration::getKinderAnzahl).orElse(0));
        stats.put("waitingListSize", waitingList.size());
        stats.put("statusCount", new HashMap<>(statusCount));
        return stats;
    }

    /**Covers R3 (Functional Interface as parameter).*/
    public List<Registration> filterRegistrations(
            Predicate<Registration> predicate) { // R3 Predicate
        return registrationRepository.findAll()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public List<String> getGastNames() {
        return registrationRepository.findGastRegistrierungen()
                .stream()
                .map(Registration::getGastName) // R3 method reference
                .filter(Objects::nonNull)
                .sorted()                        // R4 sorted
                .collect(Collectors.toList());
    }

    // ── R5 CONCURRENCY ───────────────────────────────────────

    private void sendConfirmationEmailAsync(String email, String name) {
        executorService.submit(() -> { // R5 ExecutorService
            try {
                Thread.sleep(1000); // simulate email sending delay
                System.out.println("[EMAIL] Bestätigungsmail gesendet an: "
                        + email + " (" + name + ") - Thread: "
                        + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[EMAIL] Fehler: " + e.getMessage());
            }
        });
    }


    public CompletableFuture<Map<String, Object>> getStatistikAsync() {
        return CompletableFuture.supplyAsync( // R5 CompletableFuture
                this::getStatistiken, executorService);
    }


    public void addToWaitingList(String email) {
        if (!waitingList.contains(email)) {
            waitingList.add(email); // R1 ArrayList
        }
    }


    public List<String> getWaitingList() {
        return Collections.unmodifiableList(waitingList); // R1
    }
}