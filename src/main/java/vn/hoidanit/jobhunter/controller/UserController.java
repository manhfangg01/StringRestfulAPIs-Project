package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.EmailExisted;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;
import vn.hoidanit.jobhunter.util.error.ObjectNotExisted;

import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

    }

    @GetMapping("users")
    @ApiMessage("Fetch All Users")
    public ResponseEntity<ResultPaginationDTO> getAllUsersWithPaginationAndSpecification(
            @Filter Specification<User> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.userService.fetchAllUserWithPaginationAndSpecification(spec, pageable));
    }

    @GetMapping("users/{id}")
    @ApiMessage("Fetch One Users")
    public ResponseEntity<ResUserDTO> getOneUser(
            @PathVariable("id") long id) throws ObjectNotExisted {
        Optional<User> optionalUser = this.userService.findUserById(id);
        if (optionalUser.isEmpty()) {
            throw new ObjectNotExisted("User không tồn tại trong hệ thống");
        } else {
            User realUser = optionalUser.get();
            ResUserDTO userDTO = new ResUserDTO();
            userDTO.setId(id);
            userDTO.setEmail(realUser.getEmail());
            userDTO.setName(realUser.getName());
            userDTO.setGender(realUser.getGender());
            userDTO.setAddress(realUser.getAddress());
            userDTO.setAge(realUser.getAge());
            userDTO.setUpdatedAt(realUser.getUpdatedAt());
            userDTO.setCreatedAt(realUser.getCreatedAt());
            ResUserDTO.CompanyDTO companyDTO = new ResUserDTO.CompanyDTO();
            companyDTO.setId(realUser.getCompany() != null ? realUser.getCompany().getId() : -1);
            companyDTO.setName(realUser.getCompany() != null ? realUser.getCompany().getName() : "");
            userDTO.setCompany(companyDTO);
            return ResponseEntity.ok(userDTO);
        }
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResUserDTO> createNewUser(
            @Valid @RequestBody User postManUser) throws EmailExisted {

        // Kiểm tra email đã tồn tại chưa
        if (userService.checkExistedEmail(postManUser.getEmail())) {
            throw new EmailExisted("Email tài khoản bạn tạo đã tồn tại trong hệ thống");
        }
        String hashPassword = passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hashPassword);

        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.create(postManUser));
    }

    @PutMapping("/users")
    @ApiMessage("Update user successful")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User updatedUser)
            throws ObjectNotExisted {
        if (this.userService.findUserById(updatedUser.getId()).isEmpty()) {
            throw new ObjectNotExisted("user: " + updatedUser.getId() + " không tồn tại");
        }

        return ResponseEntity.ok(this.userService.update(updatedUser));

    }

    // Patch cũng giống như Put nhưng sẽ ghi đè tất cả thông tin tin thay vi từng
    // trường -> Không an toàn, nhưng vẫn quan trọng vào khối code bên trong khối có
    // Annotation @Put
    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException, ObjectNotExisted { // Nếu
        // ResponseEntity<Void>
        // -> Không cần trả
        // về body
        if (id > 1500) {
            throw new IdInvalidException("id khong lon hon 1500");
        }
        Optional<User> optionalUser = this.userService.findUserById(id);

        if (optionalUser.isEmpty()) {
            throw new ObjectNotExisted("User không tồn tại trong hệ thống");
        } else {
            this.userService.handleDeleteUser(optionalUser.get());
        }
        return ResponseEntity.ok(null);
    }

}
