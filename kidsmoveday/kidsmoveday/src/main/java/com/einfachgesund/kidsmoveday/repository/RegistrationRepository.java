package com.einfachgesund.kidsmoveday.repository;

import com.einfachgesund.kidsmoveday.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Registration entity.
 *
 * <p>Provides CRUD operations and custom queries for the
 * 'registrations' table. Supports filtering by user, status,
 * and guest-only registrations.</p>
 *
 * <p>Covers: R6 (JDBC/Database), R7 (JPA/ORM)</p>
 *
 * @author [FirstName LastName]
 * @version 1.0
 */
@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    /**
     * Finds all registrations belonging to a specific user.
     * @param userId the ID of the user
     * @return list of registrations for that user
     */
    List<Registration> findByUserId(Long userId);

    /**
     * Finds all registrations with a given status.
     * @param status the status string (e.g. BESTAETIGT, STORNIERT)
     * @return list of matching registrations
     */
    List<Registration> findByStatus(String status);

    /**
     * Checks if a user already has a confirmed registration.
     * @param userId the user ID
     * @param status the status to check against
     * @return true if such a registration exists
     */
    boolean existsByUserIdAndStatus(Long userId, String status);

    /**
     * Custom JPQL query: sums total confirmed children registered.
     * Covers R6 - aggregation query.
     * @return total number of children in confirmed registrations
     */
    @Query("SELECT SUM(r.kinderAnzahl) FROM Registration r WHERE r.status = 'BESTAETIGT'")
    Integer sumKinderAnzahl();

    /**
     * Custom JPQL query: retrieves guest-only registrations.
     * Covers R6 - custom filter query.
     * @return list of registrations without a linked user account
     */
    @Query("SELECT r FROM Registration r WHERE r.user IS NULL")
    List<Registration> findGastRegistrierungen();
}