package com.dong.gpt.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void testGetterAndSetter() {
        // given
        Long id = 1L;
        String name = "John";
        String passwd = "password";

        User user = new User(id, name, passwd);

        // when
        String updatedName = "Mike";
        user.setName(updatedName);

        String updatedPasswd = "newPassword";
        user.setPasswd(updatedPasswd);

        // then
        Assertions.assertEquals(id, user.getId());
        Assertions.assertEquals(updatedName, user.getName());
        Assertions.assertEquals(updatedPasswd, user.getPasswd());
    }
}
