package ru.javaprojects.bxservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.bxservice.model.BasicExchange;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface BasicExchangeRepository extends JpaRepository<BasicExchange, Long> {

    Optional<BasicExchange> findByUserIdAndDate(long userId, LocalDate date);

    List<BasicExchange> findAllByUserIdAndDateGreaterThanEqual(long userId, LocalDate date);
}