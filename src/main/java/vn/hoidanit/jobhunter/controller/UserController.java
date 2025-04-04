package vn.hoidanit.jobhunter.controller;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mysql.cj.xdevapi.JsonString;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable("id") long id) {
        Optional<User> user = this.userService.findUserById(id);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    @GetMapping("/user")
    public List<User> getAllUser() {
        List<User> allUsers = this.userService.fetchAllUser();
        return allUsers;
    }

    @PostMapping("/user")
    public User CreateNewUser(@RequestBody User postManUser) {
        User user = new User();
        user.setEmail(postManUser.getEmail());
        user.setName(postManUser.getName());
        user.setPassword(postManUser.getPassword());
        User newUser = this.userService.handleSaveUser((user));
        return newUser;

    }

    @PutMapping("/user")
    public User putMethodName(@RequestBody User updatedUser) {
        Optional<User> optionalUser = this.userService.findUserById(updatedUser.getId());
        User currentUser = optionalUser.get();
        currentUser.setEmail(updatedUser.getEmail());
        currentUser.setName(updatedUser.getName());
        currentUser.setPassword(updatedUser.getPassword());
        return this.userService.handleSaveUser(currentUser);
    }

    // Patch cũng giống như Put nhưng sẽ ghi đè tất cả thông tin tin thay vi từng
    // trường -> Không an toàn, nhưng vẫn quan trọng vào khối code bên trong khối có
    // Annotation @Put
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
