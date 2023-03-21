package com.dong.gpt.service;

import com.dong.gpt.domain.User;
import com.dong.gpt.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser(Long id) {
        // 동일한 id 를 가진 유저 return
        return userRepository.findUserById(id);
    }

    @Override
    public boolean login(String name, String passwd) {
        // name 을 가진 유저 가져오기
        User user = userRepository.findUserByName(name);
        if (user == null)
            return false;

        // 비밀번호가 동일한 지에 대한 결과 return
        return user.getPasswd().equals(passwd);
    }

    @Override
    public boolean changePasswd(Long id, String passwd) {
        // 비밀번호 유효성 확인
        if (passwd.length() < 8 || passwd.length() > 16)
            return false;

        // 유저 가져오기
        User user = userRepository.findUserById(id);
        if (user == null) {
            return false;
        }

        // 유저 비밀번호 변경 후 저장
        user.setPasswd(passwd);
        userRepository.save(user);

        return true;
    }
}
