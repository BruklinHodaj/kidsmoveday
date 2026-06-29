package com.einfachgesund.kidsmoveday.controller;

import com.einfachgesund.kidsmoveday.model.Registration;
import com.einfachgesund.kidsmoveday.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for Registration management endpoints.
 *
 * <p>Exposes HTTP endpoints for registering users and guests,
 * retrieving registrations, managing the waiting list,
 * cancelling registrations, and fetching event statistics.</p>
 *
 * <p>Covers: R8 (RESTful Web Services), R5 (Concurrency via async endpoint)</p>
 *
 * @author [FirstName LastName]
 * @version 1.0
 */
@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    /**
     * Registers an insured user for Kids Move Day.
     * POST /api/registrations/user/{userId}?kinderAnzahl=2
     *
     * @param userId       the ID of the user to register
     * @param kinderAnzahl number of children attending
     * @return 201 Created with the saved registration
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<Registration> registerUser(
            @PathVariable Long userId,
            @RequestParam int kinderAnzahl) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registrationService.registerUser(userId, kinderAnzahl));
    }

    /**
     * Registers a guest participant without a user account.
     * POST /api/registrations/guest
     *
     * @param body map with gastName, gastEmail, kinderAnzahl
     * @return 201 Created with the saved registration
     */
    @PostMapping("/guest")
    public ResponseEntity<Registration> registerGuest(
            @RequestBody Map<String, Object> body) {
        String name  = (String)  body.get("gastName");
        String email = (String)  body.get("gastEmail");
        int kinder   = (Integer) body.get("kinderAnzahl");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registrationService.registerGuest(name, email, kinder));
    }

    /**
     * Returns all event registrations.
     * GET /api/registrations
     *
     * @return 200 OK with list of all registrations
     */
    @GetMapping
    public ResponseEntity<List<Registration>> getAll() {
        return ResponseEntity.ok(registrationService.getAllRegistrations());
    }

    /**
     * Returns a single registration by ID.
     * GET /api/registrations/{id}
     *
     * @param id the registration ID
     * @return 200 OK with registration, or 400 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Registration> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                registrationService.getRegistrationById(id));
    }

    /**
     * Returns all registrations for a specific user.
     * GET /api/registrations/user/{userId}
     *
     * @param userId the user ID to filter by
     * @return 200 OK with list of registrations
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Registration>> getByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                registrationService.getRegistrationsByUser(userId));
    }

    /**
     * Returns event statistics synchronously.
     * Uses R1, R3, R4 internally in RegistrationService.
     * GET /api/registrations/stats
     *
     * @return 200 OK with statistics map
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(registrationService.getStatistiken());
    }

    /**
     * Returns event statistics asynchronously via CompletableFuture.
     * Covers R5 (Concurrency - async endpoint).
     * GET /api/registrations/stats/async
     *
     * @return CompletableFuture resolving to 200 OK with statistics
     */
    @GetMapping("/stats/async")
    public CompletableFuture<ResponseEntity<Map<String, Object>>>
    getStatsAsync() { // R5 CompletableFuture
        return registrationService.getStatistikAsync()
                .thenApply(ResponseEntity::ok);
    }

    /**
     * Returns a sorted list of guest names.
     * Uses R3 method reference and R4 Stream internally.
     * GET /api/registrations/guests
     *
     * @return 200 OK with sorted guest name list
     */
    @GetMapping("/guests")
    public ResponseEntity<List<String>> getGuestNames() {
        return ResponseEntity.ok(registrationService.getGastNames());
    }

    /**
     * Returns the current waiting list.
     * Covers R1 (ArrayList - unmodifiable list).
     * GET /api/registrations/waiting-list
     *
     * @return 200 OK with list of waiting emails
     */
    @GetMapping("/waiting-list")
    public ResponseEntity<List<String>> getWaitingList() {
        return ResponseEntity.ok(registrationService.getWaitingList());
    }

    /**
     * Adds an email to the waiting list.
     * POST /api/registrations/waiting-list
     *
     * @param body map containing the email address
     * @return 200 OK with confirmation message
     */
    @PostMapping("/waiting-list")
    public ResponseEntity<Map<String, String>> addToWaitingList(
            @RequestBody Map<String, String> body) {
        registrationService.addToWaitingList(body.get("email"));
        return ResponseEntity.ok(Map.of(
                "message", "Added to waiting list",
                "email",   body.get("email")
        ));
    }

    /**
     * Cancels a registration by setting its status to STORNIERT.
     * DELETE /api/registrations/{id}
     *
     * @param id the ID of the registration to cancel
     * @return 200 OK with confirmation message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> cancel(
            @PathVariable Long id) {
        registrationService.cancelRegistration(id);
        return ResponseEntity.ok(Map.of(
                "message", "Registration cancelled",
                "id",      id.toString()
        ));
    }
}