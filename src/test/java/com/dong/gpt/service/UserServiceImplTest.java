package com.dong.gpt.service;

import static org.junit.jupiter.api.Assertions.*;

import com.dong.gpt.domain.User;
import com.dong.gpt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void testGetUser() {
        Long id = 1L;
        User user = new User(id, "testName", "testPasswd");
        when(userRepository.findUserById(id)).thenReturn(user);

        User result = userService.getUser(id);

        assertEquals(user, result);
        verify(userRepository, times(1)).findUserById(id);
    }

    @Test
    void testLogin() {
        String name = "testName";
        String passwd = "testPasswd";
        User user = new User(1L, name, passwd);
        when(userRepository.findUserByName(name)).thenReturn(user);

        assertTrue(userService.login(name, passwd));
        assertFalse(userService.login(name, "wrongPasswd"));
        verify(userRepository, times(2)).findUserByName(name);
    }

    @Test
    void testChangePasswd() {
        Long id = 1L;
        String passwd = "newPasswd";
        User user = new User(id, "testName", "testPasswd");
        when(userRepository.findUserById(id)).thenReturn(user);

        assertTrue(userService.changePasswd(id, passwd));
        assertEquals(passwd, user.getPasswd());
        verify(userRepository, times(1)).findUserById(id);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testChangePasswdWithInvalidPasswd() {
        Long id = 1L;
        String passwd = "short";
        User user = new User(id, "testName", "testPasswd");
        when(userRepository.findUserById(id)).thenReturn(user);

        assertFalse(userService.changePasswd(id, passwd));
        assertNotEquals(passwd, user.getPasswd());
        verify(userRepository, never()).findUserById(id);
        verify(userRepository, never()).save(user);
    }
}
