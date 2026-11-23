package com.lawfirm.api.repository;

import com.lawfirm.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Traemos Person en la misma consulta (JOIN FETCH) para tener el nombre del abogado en 1 solo query al loguearse
    @Query("SELECT u FROM User u JOIN FETCH u.person WHERE u.email = :email")
    Optional<User> findByEmailWithPerson(String email);

    boolean existsByEmail(String email);
}