package vn.hoidanit.jobhunter.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class JobService {
    private JobRepository jobRepository;
    private SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public Job handleSaveJob(Job job) {
        return this.jobRepository.save(job);
    }

    public ResCreateJobDTO create(Job j) {
        // check skills
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            j.setSkills(dbSkills);
        }

        // check company
        if (j.getCompany() != null) {
            Optional<Company> optionalCompany = this.companyRepository.findById(j.getCompany().getId());
            if (optionalCompany.isEmpty()) {
                j.setCompany(optionalCompany.get());

            }
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

    public ResUpdateJobDTO updateJob(Job j, Job jobInDB) {

        // Ý tưởng của hàm update sẽ lấy 2 job đó chính là job được gửi lên (j) và job
        // trong database (jobInDB)
        // check skills
        if (j.getSkills() != null) { // Lấy ra các skill của job gửi lên, tạo ra list các skill mới sau đó setSkill
                                     // ngược lại cho job trong DB
            List<Long> reqSkills = j.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            jobInDB.setSkills(dbSkills);

        }
        // check companies,Lấy ra company của Job gửi lên sau đó nếu như company tồn tại
        // thì gán lại cho job trong DB
        if (j.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(j.getCompany().getId());
            if (cOptional.isPresent()) {
                jobInDB.setCompany(cOptional.get());
            }
        }
        // update correct info : Lấy thông tin của job gửi lên gán cho job trong DB
        jobInDB.setName(j.getName());
        jobInDB.setSalary(j.getSalary());
        jobInDB.setQuantity(j.getQuantity());
        jobInDB.setLocation(j.getLocation());
        jobInDB.setLevel(j.getLevel());
        jobInDB.setStartDate(j.getStartDate());
        jobInDB.setEndDate(j.getEndDate());
        jobInDB.setActive(j.isActive());
        // update job : ghi đè job vào trong db
        Job currentJob = this.jobRepository.save(jobInDB);
        // Set response for update job: tạo ResUpdateJobDTO để trả về cho frontend
        ResUpdateJobDTO resUpdateJobDTO = new ResUpdateJobDTO();
        resUpdateJobDTO.setId(currentJob.getId());
        resUpdateJobDTO.setName(currentJob.getName());
        resUpdateJobDTO.setLocation(currentJob.getLocation());
        resUpdateJobDTO.setQuantity(0);
        resUpdateJobDTO.setLevel(currentJob.getLevel());
        resUpdateJobDTO.setStartDate(currentJob.getStartDate());
        resUpdateJobDTO.setEndDate(currentJob.getEndDate());
        resUpdateJobDTO.setActive(currentJob.isActive());
        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            resUpdateJobDTO.setSkills(skills);
        }
        return resUpdateJobDTO;
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
