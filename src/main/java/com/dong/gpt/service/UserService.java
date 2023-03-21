package com.dong.gpt.service;

import com.dong.gpt.domain.User;

public interface UserService {
    public User getUser(Long id);
    public boolean login(String name, String passwd);
    public boolean changePasswd(Long id, String passwd);
}
