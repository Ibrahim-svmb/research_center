package com.esmt.researchcenter.repository;

import com.esmt.researchcenter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET role = :role, dtype = :dtype WHERE username = :username", nativeQuery = true)
    void updateUserRole(@Param("username") String username, @Param("role") String role, @Param("dtype") String dtype);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM participants WHERE id = (SELECT id FROM users WHERE username = :username)", nativeQuery = true)
    void removeFromParticipants(@Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM gestionnaires WHERE id = (SELECT id FROM users WHERE username = :username)", nativeQuery = true)
    void removeFromGestionnaires(@Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM administrateurs WHERE id = (SELECT id FROM users WHERE username = :username)", nativeQuery = true)
    void removeFromAdministrateurs(@Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO participants (id) SELECT id FROM users WHERE username = :username", nativeQuery = true)
    void addToParticipants(@Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO gestionnaires (id, telephone) SELECT id, NULL FROM users WHERE username = :username", nativeQuery = true)
    void addToGestionnaires(@Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO administrateurs (id) SELECT id FROM users WHERE username = :username", nativeQuery = true)
    void addToAdministrateurs(@Param("username") String username);
}
