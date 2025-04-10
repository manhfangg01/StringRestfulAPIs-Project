package vn.hoidanit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.hoidanit.jobhunter.domain.User;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    public User findByEmail(String email);
}
