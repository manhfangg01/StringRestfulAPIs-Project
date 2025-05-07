package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.SkillsAndMeta;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.ObjectCollapsed;
import vn.hoidanit.jobhunter.util.error.ObjectNotExisted;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/skills")
    @ApiMessage("Fetch tất cả kĩ năng")
    public ResponseEntity<SkillsAndMeta> getAllSkills(@Filter Specification<Skill> spec, Pageable pageable) {
        return ResponseEntity.ok()
                .body(this.skillService.handleFetchAllSkillsWithSpecificationAndPagination(spec, pageable));
    }

    @GetMapping("/skills/{id}")
    @ApiMessage("Fetch kĩ năng theo id")
    public ResponseEntity<Skill> getOneSkill(@PathVariable("id") long id) throws ObjectNotExisted {
        if (this.skillService.handleFetchSkillById(id).isEmpty()) {
            throw new ObjectNotExisted("Skill không có trong danh sách");
        } else {
            return ResponseEntity.ok().body(this.skillService.handleFetchSkillById(id).get());

        }
    }

    @PostMapping("/skills")
    @ApiMessage("Thêm mới kĩ năng")
    public ResponseEntity<Skill> createNewSkills(@RequestBody Skill newSkill) throws ObjectCollapsed {
        if (this.skillService.handleFindSkillByName(newSkill.getName()).isPresent()) {
            throw new ObjectCollapsed("Skill name = " + newSkill.getName() + " đã tồn tại");
        }
        this.skillService.handleSaveSkill(newSkill);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSkill);

    }

    @PutMapping("/skills")
    @ApiMessage("Cập nhật kĩ năng")
    public ResponseEntity<Skill> updateSkill(@RequestBody Skill updatedSkill) throws ObjectNotExisted, ObjectCollapsed {
        if (this.skillService.handleFetchSkillById(updatedSkill.getId()).isEmpty()) {
            throw new ObjectNotExisted("Skill không có trong danh sách");
        }
        if (this.skillService.handleFindSkillByName(updatedSkill.getName()).isPresent()) {
            throw new ObjectCollapsed("Skill name = " + updatedSkill.getName() + " đã tồn tại");
        }
        Skill realSkill = this.skillService.handleFetchSkillById(updatedSkill.getId()).get();
        realSkill.setName(updatedSkill.getName());
        return ResponseEntity.ok().body(this.skillService.handleSaveSkill(realSkill));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws ObjectNotExisted {
        Optional<Skill> currentSkill = this.skillService.handleFetchSkillById(id);
        if (currentSkill.isEmpty()) {
            throw new ObjectNotExisted("Skill id = " + id + " không tồn tại");
        }
        this.skillService.deleteSkill(id);
        return ResponseEntity.ok().body(null);
    }

}