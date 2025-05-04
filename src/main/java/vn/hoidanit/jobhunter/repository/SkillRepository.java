package vn.hoidanit.jobhunter.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.hoidanit.jobhunter.domain.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long>, JpaSpecificationExecutor<Skill> {
    Optional<Skill> findByName(String skillName);

    boolean existsByName(String name);

    List<Skill> findByIdIn(List<Long> id);
}
