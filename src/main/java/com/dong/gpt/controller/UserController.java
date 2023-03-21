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
