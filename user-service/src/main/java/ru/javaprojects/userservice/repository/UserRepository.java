package ru.javaprojects.userservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.userservice.model.User;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllByOrderByNameAscEmail(Pageable pageable);

    Optional<User> findByEmail(String email);

    Page<User> findAllByNameContainsIgnoreCaseOrEmailContainsIgnoreCaseOrderByNameAscEmail(String nameKeyword, String emailKeyword, Pageable pageable);


}