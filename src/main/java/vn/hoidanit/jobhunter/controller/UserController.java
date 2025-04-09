package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.service.error.IdInvalidException;
import vn.hoidanit.jobhunter.service.googleService.ApiService;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ApiService apiService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, ApiService apiService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.apiService = apiService;
    }

    // @GetMapping("/api/users")
    // public ResponseEntity<List<User>> getApiData() {
    // List<User> users = apiService.getUsers();
    // return ResponseEntity.ok(users);
    // }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") long id) throws IdInvalidException {
        if (id > 1500) {
            throw new IdInvalidException("id khong lon hon 1500");
        }
        Optional<User> user = this.userService.findUserById(id);
        return ResponseEntity.ok(user.get());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUser() {
        List<User> allUsers = this.userService.fetchAllUser();
        return ResponseEntity.ok(allUsers);
    }

    @PostMapping("/users")
    public ResponseEntity<User> CreateNewUser(@RequestBody User postManUser) {
        User user = new User();
        String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
        user.setEmail(postManUser.getEmail());
        user.setName(postManUser.getName());
        user.setPassword(hashPassword);
        User newUser = this.userService.handleSaveUser((user));
        // return ResponseEntity.status(HttpStatus.CREATED).body(newUser); // Cách code
        // truyền thống
        return ResponseEntity.created(null).body(newUser); // Cách code theo builder pattern

    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User updatedUser) {
        Optional<User> optionalUser = this.userService.findUserById(updatedUser.getId());
        User currentUser = optionalUser.get();
        currentUser.setEmail(updatedUser.getEmail());
        currentUser.setName(updatedUser.getName());
        currentUser.setPassword(updatedUser.getPassword());
        User updateData = this.userService.handleSaveUser(currentUser);
        // return ResponseEntity.status(HttpStatus.OK).body(updateData);
        return ResponseEntity.ok(updateData);
    }

    // Patch cũng giống như Put nhưng sẽ ghi đè tất cả thông tin tin thay vi từng
    // trường -> Không an toàn, nhưng vẫn quan trọng vào khối code bên trong khối có
    // Annotation @Put
    @DeleteMapping("/users/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable("id") long id) throws IdInvalidException { // Nếu
                                                                                                    // ResponseEntity<Void>
                                                                                                    // -> Không cần trả
        // về body
        if (id > 1500) {
            throw new IdInvalidException("id khong lon hon 1500");
        }
        Optional<User> user = this.userService.findUserById(id);
        // return ResponseEntity.status(HttpStatus.OK).body(user.get());
        return ResponseEntity.ok(user.get());
    }

}
