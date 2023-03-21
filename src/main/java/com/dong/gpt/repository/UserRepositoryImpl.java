package com.dong.gpt.repository;

import com.dong.gpt.domain.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private EntityManager em;

    public UserRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public User findUserById(Long id) {
        return em.find(User.class, id);
    }

    @Override
    public User findUserByName(String name) {
        List<User> result = em.createQuery("select u from users u where u.name=:name", User.class)
                .setParameter("name", name)
                .getResultList();

        if (result.size() != 1) {
            return null;
        }

        return result.get(0);
    }

    @Override
    public User save(User user) {
        em.persist(user);
        return user;
    }
}
