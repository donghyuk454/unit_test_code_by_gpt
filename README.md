# Chat GPT를 사용해 단위 테스트 작성하기

## 1. 배경 설명

최근 회사에서 테스트 코드를 작성하는 방법에 대한 교육을 받았습니다. 그러던 와중에, "테스트 코드를 Chat GPT에게 작성해달라고 하면 어떻게 될까?" 라는 생각이 들어 바로 실천에 옮겼습니다. 😅

## 2. 적용 사례

### 2-1. Domain 코드에 대한 단위 테스트

다음은 제가 작성한 Domain 코드인 User.class 입니다.

User.class
---

``` 
package com.dong.gpt.domain;

import javax.persistence.*;

@Entity(name = "users")
@Table(name = "users")
public class User {

    @Id @GeneratedValue
    @Column(name="user_id", updatable = false)
    private final Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "passwd")
    private String passwd;

    public User(Long id, String name, String passwd) {
        this.id = id;
        this.name = name;
        this.passwd = passwd;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
```

다음은 User.class 에 대해 GPT 가 생성한 단위 테스트 코드입니다.


UserTest.class
---
```
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
```

사실 Getter,Setter에 대한 테스트 코드를 작성하는 것이기 때문에 여기까진 크게 문제 없이 작성할 것이라 생각하였습니다.
지금 생각해보니 만약 생성자가 다양해질 경우, 또는 빌더를 생성한 경우에는 어떻게 작성하게 될 지 궁금하네요.

### 2-2. Repository 코드에 대한 단위 테스트

이번엔 User 클래스와 DB를 연동하여 crud 기능을 맡을 UserRepository.class 인터페이스와 UserRepositoryImpl.class 구현체입니다.


UserRepository.class
---
```
package com.dong.gpt.repository;

import com.dong.gpt.domain.User;

public interface UserRepository {
    public User findUserById(Long id);
    public User findUserByName(String name);
    public User save(User user);
}
```

UserRepositoryImpl.class
---
```
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
```

이번엔 EntityManager와 JPQL이 추가되었습니다. 

과연 어떻게 작성할까요? 
GPT가 작성한 단위 테스트 코드입니다.

UserRepositoryImplTest.class
---
```
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
```

간단한 테스트이기 때문에 전 Transactional 어노테이션을 붙이지 않았는데, GPT는 Transactional 어노테이션을 붙여주었습니다.
Mocking을 사용하는 대신 @SpringBootTest를 사용하여 Transaction에 대한 테스트를 진행하는 방식입니다.
그렇다면 과연 Service에선 Mocking을 사용할까요?


## 2-3. Service 코드에 대한 단위 테스트

이번엔 User와 관련된 로직을 처리할 UserService.class 인터페이스와 구현체인 UserServiceImpl.class 입니다.


UserService.class
---
```
package com.dong.gpt.service;

import com.dong.gpt.domain.User;

public interface UserService {
    public User getUser(Long id);
    public boolean login(String name, String passwd);
    public boolean changePasswd(Long id, String passwd);
}
```


UserServiceImpl.class
---
```
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
```

GPT가 작성한 테스트 코드입니다.

UserServiceImplTest.class
---
```
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
```

드디어 Mocking을 사용하였습니다.
단위 테스트를 위해 Mockito를 사용하여 userRepository를 Mocking한 상태입니다.

## 2-4. Controller 코드에 대한 단위 테스트

마지막으로, 실제 API의 Endpoint와 Parameter 등을 결정할 Rest Controller 인 UserController.class 입니다.

UserController.class
---
```
package com.dong.gpt.controller;

import com.dong.gpt.domain.User;
import com.dong.gpt.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<User> getUser(@RequestParam(name = "user_id") Long id) {
        // id 를 가진 유저 조회
        User user = userService.getUser(id);

        if (user == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().body(user);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<User> login(@RequestParam(name = "name") String name, @RequestParam(name = "passwd") String passwd) {
        // 로그인 성공 여부 확인
        boolean isValidUserInfo = userService.login(name, passwd);

        if (isValidUserInfo) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping(path = "/passwd")
    public ResponseEntity<Void> changePasswd(@RequestParam(name = "user_id") Long id, @RequestParam(name = "passwd") String passwd) {
        // 비밀번호 변경 성공 여부 확인
        boolean isSuccess = userService.changePasswd(id, passwd);

        if (isSuccess) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
```

GPT가 작성한 단위 테스트 코드입니다.

UserControllerTest.class
---
```
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
```


이번에도 Mocking을 사용하였습니다.
게다가 이번엔 주석까지 given, when, then 형식으로 달아주었네요.


## 2-5. 테스트 커버리지 확인

마지막으로 테스트 커버리지를 확인해볼까요?

테스트 커버리지 확인을 위해 Jacoco를 사용하였습니다.

Test Report 입니다.

![테스트 결과](https://user-images.githubusercontent.com/20418155/229044947-95b26e6c-2b55-4eaa-b0be-9b438f2bcbf2.PNG)

생각했던 것보다 준수한 커버리지가 나왔습니다!


## 3. 느낀점

생각보다 테스트 코드를 잘 작성하여 놀랐습니다. 
테스트 코드를 GPT에게 모두 의존해도 될 정도의 수준은 아니지만, 테스트 코드의 틀을 만들고, 간단한 테스트 케이스에 대한 테스트 코드는 작성하는 데엔 도움이 될 것 같습니다.



다만, TDD 개발 방식에도 적합할지에 대해선 더 많은 실험이 필요할 것 같습니다.
