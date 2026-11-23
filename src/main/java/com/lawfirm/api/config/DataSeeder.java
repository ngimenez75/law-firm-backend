package com.lawfirm.api.config;

import com.lawfirm.api.entity.Person;
import com.lawfirm.api.entity.PersonType;
import com.lawfirm.api.entity.Role;
import com.lawfirm.api.entity.User;
import com.lawfirm.api.repository.PersonRepository;
import com.lawfirm.api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DataSeeder {

    @Bean
    @Transactional
    public CommandLineRunner initDefaultAdmin(
            UserRepository userRepository,
            PersonRepository personRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            String defaultEmail = "admin@estudio.com";
            if (!userRepository.existsByEmail(defaultEmail)) {
                Person person = new Person();
                person.setPersonType(PersonType.FISICA);
                person.setFirstName("Dra.");
                person.setLastName("Gaston");
                person.setEmail(defaultEmail);
                personRepository.save(person);

                User admin = new User();
                admin.setPerson(person);
                admin.setEmail(defaultEmail);
                admin.setPasswordHash(passwordEncoder.encode("Admin123!"));
                admin.setRole(Role.ROLE_ADMIN);
                userRepository.save(admin);

                System.out.println(">>> Usuario inicial creado: admin@estudio.com / Admin123!");
            }
        };
    }
}
