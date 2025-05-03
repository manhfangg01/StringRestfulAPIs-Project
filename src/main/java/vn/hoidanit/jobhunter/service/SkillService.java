package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class SkillService {
    private SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Optional<Skill> handleFindSkillByName(String skillName) {
        return this.skillRepository.findByName(skillName);
    }

    public Skill handleSaveSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public List<Skill> handleFetchAllSkills() {
        return this.skillRepository.findAll();
    }

    public List<Skill> handleFetchAllSkillsWithSpecificationAndPagination(Specification<Skill> specification,
            Pageable pageable) {
        Page<Skill> skillPage = this.skillRepository.findAll(specification, pageable);
        List<Skill> skills = skillPage.getContent();
        return skills;

    }

    public Optional<Skill> handleFetchSkillById(long id) {
        return this.skillRepository.findById(id);
    }

}
