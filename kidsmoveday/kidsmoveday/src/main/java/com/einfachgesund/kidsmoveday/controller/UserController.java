package com.einfachgesund.kidsmoveday.controller;

import com.einfachgesund.kidsmoveday.model.User;
import com.einfachgesund.kidsmoveday.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for User management endpoints.
 *
 * <p>Exposes HTTP endpoints for creating, retrieving, updating,
 * and deleting users. Also provides login and statistics endpoints.
 * All responses are in JSON format.</p>
 *
 * <p>Covers: R8 (RESTful Web Services)</p>
 *
 * @author [FirstName LastName]
 * @version 1.0
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Creates a new user account.
     * POST /api/users
     *
     * @param user the user data from request body
     * @return 201 Created with the saved user
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(user));
    }

    /**
     * Returns all registered users.
     * GET /api/users
     *
     * @return 200 OK with list of all users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Returns a user by their database ID.
     * GET /api/users/{id}
     *
     * @param id the user ID from path
     * @return 200 OK with user, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Returns a user by their Versicherungsnummer.
     * GET /api/users/versicherung/{nummer}
     *
     * @param nummer the insurance number from path
     * @return 200 OK with user, or 404 if not found
     */
    @GetMapping("/versicherung/{nummer}")
    public ResponseEntity<User> getByVersicherungsnummer(
            @PathVariable String nummer) {
        return ResponseEntity.ok(
                userService.getUserByVersicherungsnummer(nummer));
    }

    /**
     * Returns only users with active insurance policies.
     * Uses R3 Lambda (Predicate) and R4 Stream internally.
     * GET /api/users/versicherte
     *
     * @return 200 OK with filtered list of insured users
     */
    @GetMapping("/versicherte")
    public ResponseEntity<List<User>> getVersicherte() {
        return ResponseEntity.ok(userService.getVersicherteUsers());
    }

    /**
     * Returns a sorted list of all user emails.
     * Uses R3 method reference and R4 Stream internally.
     * GET /api/users/emails
     *
     * @return 200 OK with sorted email list
     */
    @GetMapping("/emails")
    public ResponseEntity<List<String>> getAllEmails() {
        return ResponseEntity.ok(userService.getAllEmails());
    }

    /**
     * Returns user statistics including totals and newest user.
     * Uses R1 HashMap, R3 Comparator, and R4 Stream internally.
     * GET /api/users/stats
     *
     * @return 200 OK with statistics map
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(userService.getUserStats());
    }

    /**
     * Updates an existing user's data.
     * PUT /api/users/{id}
     *
     * @param id      the ID of the user to update
     * @param updated the new user data from request body
     * @return 200 OK with the updated user
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id,
                                           @RequestBody User updated) {
        return ResponseEntity.ok(userService.updateUser(id, updated));
    }

    /**
     * Deletes a user by their ID.
     * DELETE /api/users/{id}
     *
     * @param id the ID of the user to delete
     * @return 200 OK with confirmation message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of(
                "message", "User deleted successfully",
                "id", id.toString()
        ));
    }

    /**
     * Authenticates a user with Versicherungsnummer and password.
     * POST /api/users/login
     *
     * @param body map containing versicherungsnummer and passwort
     * @return 200 OK with user info, or 401 if credentials are wrong
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> body) {
        String nummer   = body.get("versicherungsnummer");
        String passwort = body.get("passwort");
        User user = userService.getUserByVersicherungsnummer(nummer);
        if (!user.getPasswort().equals(passwort)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
        return ResponseEntity.ok(Map.of(
                "message",       "Login successful",
                "userId",        user.getId(),
                "name",          user.getName(),
                "istVersichert", user.isIstVersichert()
        ));
    }
    /**
     * Returns all currently stored users — for Admin dashboard.
     * GET /api/users/admin/all
     */
    @GetMapping("/admin/all")
    public ResponseEntity<List<User>> getAllForAdmin() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}