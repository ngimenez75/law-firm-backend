package com.lawfirm.api.repository;

import com.lawfirm.api.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {
}