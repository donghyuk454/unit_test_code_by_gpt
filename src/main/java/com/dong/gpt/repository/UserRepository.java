package com.dong.gpt.repository;

import com.dong.gpt.domain.User;

public interface UserRepository {
    public User findUserById(Long id);
    public User findUserByName(String name);
    public User save(User user);
}
