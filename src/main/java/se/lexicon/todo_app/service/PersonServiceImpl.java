package se.lexicon.todo_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.todo_app.dto.PersonDto;
import se.lexicon.todo_app.dto.PersonRegistrationDto;
import se.lexicon.todo_app.entity.Person;
import se.lexicon.todo_app.entity.Role;
import se.lexicon.todo_app.entity.User;
import se.lexicon.todo_app.repository.PersonRepository;
import se.lexicon.todo_app.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<PersonDto> findAll() {
        return personRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PersonDto findById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));
        return toDto(person);
    }

    @Override
    public PersonDto create(PersonRegistrationDto registrationDto) {
        if (personRepository.existsByEmail(registrationDto.email())) {
            throw new RuntimeException("Email already exists: " + registrationDto.email());
        }
        if (userRepository.existsByUsername(registrationDto.username())) {
            throw new RuntimeException("Username already exists: " + registrationDto.username());
        }

        User user = new User(
                registrationDto.username(),
                passwordEncoder.encode(registrationDto.password())
        );
        user.addRole(Role.USER);

        Person person = new Person(
                registrationDto.name(),
                registrationDto.email()
        );
        person.setUser(user);

        Person saved = personRepository.save(person);
        return toDto(saved);
    }

    @Override
    public PersonDto update(Long id, PersonDto dto) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));

        person.setName(dto.name());
        person.setEmail(dto.email());

        if (person.getUser() != null && dto.username() != null) {
            person.getUser().setUsername(dto.username());
        }

        Person updated = personRepository.save(person);
        return toDto(updated);
    }

    @Override
    public void delete(Long id) {
        if (!personRepository.existsById(id)) {
            throw new RuntimeException("Person not found with id: " + id);
        }
        personRepository.deleteById(id);
    }

    @Override
    public PersonDto findByEmail(String email) {
        Person person = personRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Person not found with email: " + email));
        return toDto(person);
    }

    @Override
    public void updatePassword(Long id, String newPassword) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));

        if (person.getUser() == null) {
            throw new RuntimeException("No user account linked to person id: " + id);
        }

        person.getUser().setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(person.getUser());
    }

    @Override
    public void toggleExpired(Long id, boolean status) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));

        if (person.getUser() == null) {
            throw new RuntimeException("No user account linked to person id: " + id);
        }

        person.getUser().setExpired(status);
        userRepository.save(person.getUser());
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return personRepository.existsByEmail(email);
    }

    @Override
    public void addRole(Long id, Role role) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));

        if (person.getUser() == null) {
            throw new RuntimeException("No user account linked to person id: " + id);
        }

        person.getUser().addRole(role);
        userRepository.save(person.getUser());
    }

    @Override
    public void removeRole(Long id, Role role) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));

        if (person.getUser() == null) {
            throw new RuntimeException("No user account linked to person id: " + id);
        }

        person.getUser().removeRole(role);
        userRepository.save(person.getUser());
    }

    // 🔹 Mapper
    private PersonDto toDto(Person person) {
        String roles = (person.getUser() != null)
                ? person.getUser().getRoles().toString()
                : "NO_ROLE";

        return new PersonDto(
                person.getId(),
                person.getName(),
                person.getEmail(),
                (person.getUser() != null) ? person.getUser().getUsername() : null,
                roles
        );
    }
}
