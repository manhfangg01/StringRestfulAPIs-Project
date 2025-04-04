package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;

@RestController
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @GetMapping("/user/create")
    // public String
    @PostMapping("/user")
    public String CreateNewUser(@RequestBody User postManUser) {
        User user = new User();
        user.setEmail(postManUser.getEmail());
        user.setName(postManUser.getName());
        user.setPassword(postManUser.getPassword());
        User newUser = this.userService.handleSaveUser((user));
        return newUser.toString();

    }

    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        Optional<User> user = this.userService.findUserById(id);
        if (user.isPresent()) {
            this.userService.handleDeleteUser(user.get());
        } else {
            return "xoa k dc";
        }
        return "xoa thanh cong";
    }

}
