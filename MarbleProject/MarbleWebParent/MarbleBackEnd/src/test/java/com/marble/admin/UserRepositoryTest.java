package com.marble.admin;

import com.marble.admin.user.UserRepository;
import com.marble.common.entity.Role;
import com.marble.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTest {
    @Autowired
    private UserRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateUserWithOneRole() {
        Role roleAdmin = entityManager.find(Role.class, 1);
        User userLongHo = new User("long.ho@tcu.edu", "Long123@", "Long", "Ho");
        userLongHo.addRole(roleAdmin);

        User savedUser = repo.save(userLongHo);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateUserWithMultipleRoles() {
        User userQuang = new User("quang@gmail.com","quang123","Quang","Ho");
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);

        userQuang.addRole(roleEditor);
        userQuang.addRole(roleAssistant);

        User savedUser = repo.save(userQuang);
    }
    @Test
    public void testListAllUsers() {
        Iterable<User> listUsers = repo.findAll();
        listUsers.forEach(user -> System.out.println(user));
    }

    @Test
    public void testGetUserById() {
        User userLong = repo.findById(1).get();
        System.out.println(userLong);
        assertThat(userLong).isNotNull();
    }

    @Test
    public void testUpdateUserDetails() {
        User userLong = repo.findById(1).get();
        userLong.setEnabled(true);
        userLong.setEmail("hoquanglong0910@gmail.com");

        repo.save(userLong);
    }

    @Test
    public void testUpdateUserRoles() {
        User userQuang = repo.findById(2).get();
        Role roleEditor = new Role(3);
        // get Set roles of userQuang, then remove the role Editor
        userQuang.getRoles().remove(roleEditor);
        // new Role(2) create new role of Salesperson
        userQuang.getRoles().add(new Role(2));
    }
    @Test
    public void testDeleteUser() {
        Integer userId = 2;
        repo.deleteById(userId);
    }

    @Test
    public void testGetUserByEmail() {
        String email = "hoquanglong0910@gmail.com";
        User user = repo.getUserByEmail(email);

        assertThat(user).isNotNull();
    }

    @Test
    public void testCountById() {
        Integer id = 1;
        Long countById = repo.countById(id);

        assertThat(countById).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testDisableUser() {
        Integer id = 1;
        repo.updateEnabledStatus(id, true);
    }
}
