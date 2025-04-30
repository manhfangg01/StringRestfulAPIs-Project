package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.dto.UserDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

        Meta meta = new Meta();
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

        // Chuyển đổi sang DTO
        List<UserDTO> userDTOs = userPage.getContent()
                .stream()
                .map(this::convertToUserDTO)
                .toList();

        // Tạo metadata phân trang
        Meta meta = new Meta();
        meta.setPage(userPage.getNumber() + 1); // PageNumber bắt đầu từ 0
        meta.setPageSize(userPage.getSize());
        meta.setPages(userPage.getTotalPages());
        meta.setTotal(userPage.getTotalElements()); // Không trừ 1

        return new ResultPaginationDTO(meta, userDTOs);
    }

    // Phương thức chuyển đổi User -> UserDTO
    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());
        dto.setAddress(user.getAddress());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt()); // Lưu ý chính tả (UpdatedAt -> updatedAt)
        return dto;
    }

    public boolean checkExistedEmail(String email) {
        return this.userRepository.findByEmail(email) != null;
    }

}
