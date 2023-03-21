package com.dong.gpt.controller;

import com.dong.gpt.domain.User;
import com.dong.gpt.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService);
    }

    @Test
    void getUser_shouldReturnUser_whenUserExist() {
        //given
        Long userId = 1L;
        User user = new User(userId, "test", "test");
        when(userService.getUser(userId)).thenReturn(user);

        //when
        ResponseEntity<User> response = userController.getUser(userId);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void getUser_shouldReturnBadRequest_whenUserDoesNotExist() {
        //given
        Long userId = 1L;
        when(userService.getUser(userId)).thenReturn(null);

        //when
        ResponseEntity<User> response = userController.getUser(userId);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void login_shouldReturnOk_whenUserInfoIsValid() {
        //given
        String name = "test";
        String passwd = "test";
        when(userService.login(name, passwd)).thenReturn(true);

        //when
        ResponseEntity<User> response = userController.login(name, passwd);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void login_shouldReturnBadRequest_whenUserInfoIsInvalid() {
        //given
        String name = "test";
        String passwd = "test";
        when(userService.login(name, passwd)).thenReturn(false);

        //when
        ResponseEntity<User> response = userController.login(name, passwd);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void changePasswd_shouldReturnOk_whenPasswdIsChanged() {
        //given
        Long userId = 1L;
        String passwd = "test";
        when(userService.changePasswd(userId, passwd)).thenReturn(true);

        //when
        ResponseEntity<Void> response = userController.changePasswd(userId, passwd);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void changePasswd_shouldReturnBadRequest_whenPasswdIsNotChanged() {
        //given
        Long userId = 1L;
        String passwd = "test";
        when(userService.changePasswd(userId, passwd)).thenReturn(false);

        //when
        ResponseEntity<Void> response = userController.changePasswd(userId, passwd);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}