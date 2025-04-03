package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;

@RestController
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/create")
    public String CreateNewUser() {
        User user = new User();
        user.setEmail("manh@gmail.com");
        user.setName("manh");
        user.setPassword("manh123");

        this.userService.handleSaveUser((user));
        return "cu";
    }
}
