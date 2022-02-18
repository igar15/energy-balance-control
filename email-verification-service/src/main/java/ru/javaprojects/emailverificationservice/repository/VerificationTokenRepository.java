package ru.javaprojects.emailverificationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.emailverificationservice.model.VerificationToken;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("DELETE FROM VerificationToken v WHERE v.email = :email")
    void deleteByEmail(@Param("email") String email);
}