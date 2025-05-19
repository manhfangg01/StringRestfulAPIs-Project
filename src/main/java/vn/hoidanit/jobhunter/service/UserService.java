package vn.hoidanit.jobhunter.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.ResumeRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {

    private final ResumeRepository resumeRepository;
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CompanyService companyService,
            RoleService roleService, ResumeRepository resumeRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyService = companyService;
        this.roleService = roleService;
        this.resumeRepository = resumeRepository;
    }

    public ResUserDTO create(User user) {

        // Check Company
        if (user.getCompany() != null) {
            Optional<Company> comOptional = this.companyService.handleFetchCompanyById(user.getCompany().getId());
            user.setCompany(comOptional.isPresent() ? comOptional.get() : null);
        }

        // Check Role
        if (user.getRole() != null) {
            Optional<Role> roleOptional = this.roleService.handleFindRoleById(user.getRole().getId());
            user.setRole(roleOptional.isPresent() ? roleOptional.get() : null);
        }
        this.userRepository.save(user);

        ResUserDTO res = new ResUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setCreatedAt(res.getCreatedAt());
        ResUserDTO.CompanyDTO com = new ResUserDTO.CompanyDTO();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompany(com);

        }

        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            res.setRoleUser(roleUser);
        }
        res.setCompany(com);
        res.setRoleUser(roleUser);
        return res;
    }

    public ResUpdateUserDTO update(User user) {
        User dbUser = this.userRepository.findById(user.getId()).get();
        // Check Company
        if (user.getCompany() != null) {
            Optional<Company> comOptional = this.companyService.handleFetchCompanyById(user.getCompany().getId());
            dbUser.setCompany(comOptional.isPresent() ? comOptional.get() : null);
        }

        // Check Role
        if (user.getRole() != null) {
            Optional<Role> roleOptional = this.roleService.handleFindRoleById(user.getRole().getId());
            dbUser.setRole(roleOptional.isPresent() ? roleOptional.get() : null);
        }

        dbUser.setName(user.getName());
        dbUser.setAddress(user.getAddress());
        dbUser.setAge(user.getAge());
        dbUser.setGender(user.getGender());
        this.userRepository.save(dbUser);

        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        ResUpdateUserDTO.CompanyDTO com = new ResUpdateUserDTO.CompanyDTO();
        ResUpdateUserDTO.RoleUser roleUser = new ResUpdateUserDTO.RoleUser();
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompany(com);

        }

        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            res.setRoleUser(roleUser);
        }
        res.setCompany(com);
        res.setRoleUser(roleUser);

        return res;

    }

    public User handleSaveUser(User user) {
        return this.userRepository.save(user);
    }

    public void handleDeleteUser(User user) {
        this.userRepository.delete(user);
    }

    public Optional<User> findUserById(long id) {
        return this.userRepository.findById(id);
    }

    public List<User> fetchAllUser() {
        return this.userRepository.findAll();
    }

    public Page<User> fetchAllUserWithPagination(Pageable pageable) {
        return this.userRepository.findAll(pageable);

    }

    public ResultPaginationDTO fetchAllUserWithoutPagination() {
        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(null);
        result.setResult(this.fetchAllUser()); // gọi phương thức cũ
        return result;
    }

    public User handleGetUserByUserName(String username) {
        return this.userRepository.findByEmail(username);
    }

    public ResultPaginationDTO fetchAllUserWithPagination(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<User> pageUser = this.fetchAllUserWithPagination(pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageUser.getNumber() + 1);
        meta.setPageSize(pageUser.getSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(pageUser.getContent());
        return result;
    }

    public ResultPaginationDTO fetchAllUserWithPaginationAndSpecification(Specification<User> spec, Pageable pageable) {
        // Lấy dữ liệu phân trang từ repository
        Page<User> userPage = this.userRepository.findAll(spec, pageable);

        // remove sensitive data
        List<ResUserDTO> listUser = userPage.getContent()
                .stream().map(item -> this.convertToUserDTO(item))
                .collect(Collectors.toList());

        // Tạo metadata phân trang
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(userPage.getNumber() + 1); // PageNumber bắt đầu từ 0
        meta.setPageSize(userPage.getSize());
        meta.setPages(userPage.getTotalPages());
        meta.setTotal(userPage.getTotalElements()); // Không trừ 1

        // Gán trường cho Company

        return new ResultPaginationDTO(meta, listUser);
    }

    // Phương thức chuyển đổi User -> UserDTO
    private ResUserDTO convertToUserDTO(User user) {
        ResUserDTO dto = new ResUserDTO();
        ResUserDTO.CompanyDTO com = new ResUserDTO.CompanyDTO();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());
        dto.setAddress(user.getAddress());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt()); // Lưu ý chính tả (UpdatedAt -> updatedAt)
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            dto.setCompany(com);

        }

        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            dto.setRoleUser(roleUser);
        }
        return dto;
    }

    public boolean checkExistedEmail(String email) {
        return this.userRepository.findByEmail(email) != null;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUserName(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User fetchUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }

    public long handleCountUserInCompanyById(Company company) {
        List<User> users = this.userRepository.findByCompany(company);
        return users.size();
    }

}
