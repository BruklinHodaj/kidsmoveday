package com.einfachgesund.kidsmoveday.service;

import com.einfachgesund.kidsmoveday.exception.RegistrationException;
import com.einfachgesund.kidsmoveday.exception.UserNotFoundException;
import com.einfachgesund.kidsmoveday.model.User;
import com.einfachgesund.kidsmoveday.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final Map<String, User> userCache = new HashMap<>();

    /** R1 */
    private final Set<String> registeredNumbers = new TreeSet<>();

    public User createUser(User user) {
        if (userRepository.existsByVersicherungsnummer(
                user.getVersicherungsnummer())) {
            throw new RegistrationException(
                    "Versicherungsnummer bereits vergeben: "
                            + user.getVersicherungsnummer(),
                    "DUPLICATE_VERSICHERUNGSNUMMER"
            );
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RegistrationException(
                    "E-Mail bereits registriert: " + user.getEmail(),
                    "DUPLICATE_EMAIL"
            );
        }

        User saved = userRepository.save(user);
        userCache.put(saved.getVersicherungsnummer(), saved);  // R1 HashMap
        registeredNumbers.add(saved.getVersicherungsnummer()); // R1 TreeSet
        return saved;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User getUserByVersicherungsnummer(String nummer) {
        if (userCache.containsKey(nummer)) {  // R1 - HashMap cache hit
            return userCache.get(nummer);
        }
        User user = userRepository.findByVersicherungsnummer(nummer)
                .orElseThrow(() -> new UserNotFoundException(
                        "Kein Benutzer mit Versicherungsnummer: " + nummer
                ));
        userCache.put(nummer, user);
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public User updateUser(Long id, User updated) {
        User existing = getUserById(id);
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setIstVersichert(updated.isIstVersichert());
        userCache.remove(existing.getVersicherungsnummer()); // R1 cache invalidate
        return userRepository.save(existing);
    }


    public void deleteUser(Long id) {
        User user = getUserById(id);
        userCache.remove(user.getVersicherungsnummer());        // R1 HashMap
        registeredNumbers.remove(user.getVersicherungsnummer()); // R1 TreeSet
        userRepository.deleteById(id);
    }

    /**R3 LAMBDA + R4 STREAM */

    public List<User> getVersicherteUsers() {
        Predicate<User> istVersichert = user -> user.isIstVersichert(); // R3
        return userRepository.findAll()
                .stream()
                .filter(istVersichert)       // R4 - filter
                .collect(Collectors.toList());
    }


    public List<String> getAllEmails() {
        Function<User, String> toEmail = User::getEmail; // R3 method reference
        return userRepository.findAll()
                .stream()
                .map(toEmail)   // R4 - map
                .sorted()       // R4 - sorted
                .collect(Collectors.toList());
    }


    public <T> List<T> filterList(List<T> list, Predicate<T> predicate) { // R2
        return list.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }


    public Map<String, Object> getUserStats() {
        List<User> all = userRepository.findAll();

        long totalUsers = all.stream().count(); // R4

        long versicherteCount = all.stream()
                .filter(User::isIstVersichert)  // R4 filter
                .count();

        Optional<User> newest = all.stream()
                .max(Comparator.comparing(User::getErstelltAm)); // R3 Comparator

        Map<String, Object> stats = new HashMap<>(); // R1 HashMap
        stats.put("totalUsers", totalUsers);
        stats.put("versicherteUsers", versicherteCount);
        stats.put("gastUsers", totalUsers - versicherteCount);
        stats.put("newestUser", newest.map(User::getName).orElse("N/A"));
        stats.put("registeredNumbers", new ArrayList<>(registeredNumbers));
        return stats;
    }


    public <T extends Comparable<T>> T getMax(List<T> list) { // R2 bounded generic
        return list.stream()
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new RuntimeException("List is empty"));
    }
}