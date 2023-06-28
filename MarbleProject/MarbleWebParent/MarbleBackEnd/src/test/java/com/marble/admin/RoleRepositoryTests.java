package com.marble.admin;

import com.marble.admin.user.RoleRepository;
import com.marble.common.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RoleRepositoryTests {
    @Autowired
    private RoleRepository repo;

    @Test
    public void testCreateFirstRole() {
        Role roleAdmin = new Role("Admin", "manage everything");
        Role saveRole = repo.save(roleAdmin);
        assertThat(saveRole.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateRestRoles() {
        Role roleSalesperson = new Role("Salesperson", "manage product price, customers, " +
                "shipping, orders and sales report");
        Role roleEditor = new Role("Editor", "manage categories, brands, products, " +
                "articles, and menus");
        Role roleShipper = new Role("Shipper", "view products, view orders " +
                "and update order status");
        Role roleAssistant = new Role("Assistant", "manage " +
                "questions ans reviews");
        repo.saveAll(List.of(roleShipper,roleEditor,roleAssistant,roleSalesperson));
    }
}
