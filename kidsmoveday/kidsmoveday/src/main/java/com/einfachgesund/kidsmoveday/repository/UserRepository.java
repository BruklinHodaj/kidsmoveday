package com.einfachgesund.kidsmoveday.repository;

import com.einfachgesund.kidsmoveday.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for User entity.
 *
 * <p>Provides CRUD operations and custom queries for the 'users' table.
 * Extends JpaRepository to inherit standard database operations.</p>
 *
 * <p>Covers: R6 (JDBC/Database), R7 (JPA/ORM)</p>
 *
 * @author [FirstName LastName]
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique insurance number.
     * @param versicherungsnummer the insurance number to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByVersicherungsnummer(String versicherungsnummer);

    /**
     * Finds a user by their email address.
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the given insurance number already exists.
     * @param versicherungsnummer the insurance number to check
     * @return true if exists, false otherwise
     */
    boolean existsByVersicherungsnummer(String versicherungsnummer);

    /**
     * Checks if a user with the given email already exists.
     * @param email the email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Custom JPQL query to retrieve only active insured users.
     * Covers R6 - custom database query.
     * @return list of users with active insurance
     */
    @Query("SELECT u FROM User u WHERE u.istVersichert = true")
    List<User> findAllVersichert();
}