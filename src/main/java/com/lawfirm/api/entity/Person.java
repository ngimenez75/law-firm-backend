package com.lawfirm.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "person_type", nullable = false, length = 20)
    private PersonType personType = PersonType.FISICA;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "business_name", length = 200)
    private String businessName;

    @Column(name = "tax_id", unique = true, length = 20)
    private String taxId;

    @Column(length = 150)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Person() {}

    // Helper para obtener el nombre completo en el login
    public String getFullName() {
        if (personType == PersonType.JURIDICA && businessName != null) {
            return businessName;
        }
        return ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
    }
}
