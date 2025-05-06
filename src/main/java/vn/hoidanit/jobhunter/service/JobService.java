package vn.hoidanit.jobhunter.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class JobService {
    private JobRepository jobRepository;
    private SkillRepository skillRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public Job handleSaveJob(Job job) {
        return this.jobRepository.save(job);
    }

    public vn.hoidanit.jobhunter.domain.response.ResCreateJobDTO create(Job j) {
        // check skills
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            j.setSkills(dbSkills);

        }

        // create job
        Job currentJob = this.jobRepository.save(j);
        // convert response
        vn.hoidanit.jobhunter.domain.response.ResCreateJobDTO dto = new vn.hoidanit.jobhunter.domain.response.ResCreateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setCreatedAt(currentJob.getCreatedAt());
        dto.setCreatedBy(currentJob.getCreatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }
        return dto;

    }

    // Update Job
    public Optional<Job> handleFetchJobById(long id) {
        return this.jobRepository.findById(id);
    }

    public ResUpdateJobDTO updateJob(Job updatedJob) {
        // 2. Cập nhật các trường cơ bản
        Job newJob = new Job();
        newJob.setName(updatedJob.getName());
        newJob.setLocation(updatedJob.getLocation());
        newJob.setQuantity(updatedJob.getQuantity());
        newJob.setSalary(updatedJob.getSalary());
        newJob.setLevel(updatedJob.getLevel());
        newJob.setDescription(updatedJob.getDescription());
        newJob.setStartDate(updatedJob.getStartDate());
        newJob.setEndDate(updatedJob.getEndDate());
        newJob.setActive(updatedJob.isActive());

        // 3. Xử lý cập nhật skills
        if (updatedJob.getSkills() != null) {
            List<Long> reqSkillIds = updatedJob.getSkills()
                    .stream()
                    .map(Skill::getId)
                    .collect(Collectors.toList());

            // Lấy danh sách skill mới từ database
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkillIds);

            // Cập nhật skills cho job
            newJob.setSkills(dbSkills);
        } else {
            // Nếu không gửi skills trong request, giữ nguyên skills hiện tại
            // Hoặc có thể xóa hết skills nếu muốn:
            // existingJob.setSkills(null);
        }

        // 4. Lưu job đã cập nhật
        Job savedJob = this.jobRepository.save(newJob);

        // 5. Convert sang DTO để trả về
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(savedJob.getId());
        dto.setName(savedJob.getName());
        dto.setSalary(savedJob.getSalary());
        dto.setQuantity(savedJob.getQuantity());
        dto.setLocation(savedJob.getLocation());
        dto.setLevel(savedJob.getLevel());
        dto.setStartDate(savedJob.getStartDate());
        dto.setEndDate(savedJob.getEndDate());
        dto.setActive(savedJob.isActive());
        dto.setCreatedAt(savedJob.getCreatedAt());
        dto.setCreatedBy(savedJob.getCreatedBy());
        dto.setUpdatedAt(Instant.now());
        dto.setUpdatedBy(Instant.now());

        // Xử lý skills trong response
        if (savedJob.getSkills() != null) {
            List<String> skillNames = savedJob.getSkills()
                    .stream()
                    .map(Skill::getName)
                    .collect(Collectors.toList());
            dto.setSkills(skillNames);
        }

        return dto;
    }

    public void handleDeteleById(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO handleFetchAllJobsWithSpecificationAndPagination(Specification<Job> spec,
            Pageable pageable) {
        ResultPaginationDTO resJobsAndMeta = new ResultPaginationDTO();
        Page<Job> jobPage = this.jobRepository.findAll(spec, pageable);
        List<Job> jobs = jobPage.getContent();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(jobPage.getNumber() + 1); // PageNumber bắt đầu từ 0
        meta.setPageSize(jobPage.getSize());
        meta.setPages(jobPage.getTotalPages());
        meta.setTotal(jobPage.getTotalElements()); // Không trừ 1
        resJobsAndMeta.setResult(jobs);
        resJobsAndMeta.setMeta(meta);
        return resJobsAndMeta;
    }

}
