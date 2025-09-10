package se.lexicon.todo_app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import se.lexicon.todo_app.entity.*;
import se.lexicon.todo_app.repository.AttachmentRepository;
import se.lexicon.todo_app.repository.PersonRepository;
import se.lexicon.todo_app.repository.TodoRepository;

@Configuration
public class DataInitializer {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner run(PersonRepository personRepo, TodoRepository todoRepo, AttachmentRepository attachmentRepo) {
        return args -> {
            if (personRepo.count() == 0 && todoRepo.count() == 0 && attachmentRepo.count() == 0) {
                // 👨‍💻 Create Developers


                System.out.println("Creating initial users...");

                Person adminPerson = new Person("Admin", "fida.boufares@gmail.com");
                User admin = new User("admin", passwordEncoder.encode("password"));
                admin.addRole(Role.USER);
                admin.addRole(Role.ADMIN);
                adminPerson.setUser(admin);
                Person savedAdmin = personRepo.save(adminPerson);
                System.out.println("Admin user created with username: " + savedAdmin.getUser().getUsername());


                Person person1 = new Person("User1", "user1@test.se");
                User user1 = new User("user1", passwordEncoder.encode("password"));
                user1.addRole(Role.USER);
                person1.setUser(user1);
                Person savedUser1 = personRepo.save(person1);
                System.out.println("User1 created with username: " + savedUser1.getUser().getUsername());

                Person person2 = new Person("User2", "user2@test.se");
                User user2 = new User("user2", passwordEncoder.encode("password"));
                user2.addRole(Role.USER);
                person2.setUser(user2);
                Person savedUser2 = personRepo.save(person2);
                System.out.println("User2 created with username: " + savedUser2.getUser().getUsername());





            }
        };
    }
}