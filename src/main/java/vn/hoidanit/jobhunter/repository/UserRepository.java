package vn.hoidanit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.hoidanit.jobhunter.domain.User;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    public User findByEmail(String email);
    
}
