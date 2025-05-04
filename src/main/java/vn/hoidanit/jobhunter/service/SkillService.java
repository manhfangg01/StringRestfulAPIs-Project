package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.SkillsAndMeta;
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

    public SkillsAndMeta handleFetchAllSkillsWithSpecificationAndPagination(Specification<Skill> specification,
            Pageable pageable) {
        SkillsAndMeta skillsAndMeta = new SkillsAndMeta();
        Page<Skill> skillPage = this.skillRepository.findAll(specification, pageable);
        List<Skill> skills = skillPage.getContent();
        SkillsAndMeta.Meta meta = new SkillsAndMeta.Meta();
        meta.setPage(skillPage.getNumber() + 1); // PageNumber bắt đầu từ 0
        meta.setPageSize(skillPage.getSize());
        meta.setPages(skillPage.getTotalPages());
        meta.setTotal(skillPage.getTotalElements()); // Không trừ 1
        skillsAndMeta.setMeta(meta);
        skillsAndMeta.setSkills(skills);
        return skillsAndMeta;

    }

    public void deleteSkill(long id) {
        // delete job (inside job_skill table)
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        Skill currentSkill = skillOptional.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));

        // delete skill
        this.skillRepository.delete(currentSkill);
    }

    public Optional<Skill> handleFetchSkillById(long id) {
        return this.skillRepository.findById(id);
    }

    // Xóa skill thì sẽ tìm vào các Jobs có skill đó rồi remove nó đi

}
