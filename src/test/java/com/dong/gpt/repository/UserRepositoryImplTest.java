package com.dong.gpt.repository;

import com.dong.gpt.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
class UserRepositoryImplTest {

    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    @PersistenceContext
    private EntityManager em;

    @Test
    void testFindUserById() {
        User user = new User(null, "John", "password");
        em.persist(user);

        User result = userRepositoryImpl.findUserById(user.getId());

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getPasswd(), result.getPasswd());
    }

    @Test
    void testFindUserByName() {
        User user = new User(null, "John", "password");
        em.persist(user);

        User result = userRepositoryImpl.findUserByName(user.getName());

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getPasswd(), result.getPasswd());

        result = userRepositoryImpl.findUserByName("Noexistent");
        assertNull(result);
    }

    @Test
    void testSave() {
        User user = new User(null, "John", "password");
        userRepositoryImpl.save(user);

        User result = em.find(User.class, user.getId());

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getPasswd(), result.getPasswd());
    }
}
