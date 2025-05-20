package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.request.ReqUpdateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.ResCreateResume;
import vn.hoidanit.jobhunter.domain.response.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.service.googleService.ApiService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.ObjectNotExisted;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserService userService;
    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeController(ResumeService resumeService, UserService userService, FilterBuilder filterBuilder,
            FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("get one user with id")
    public ResponseEntity<ResFetchResumeDTO> fetchOneResume(@PathVariable("id") long id) throws ObjectNotExisted {
        Optional<Resume> reqResumeOptional = this.resumeService.handleFetchResumeById(id);
        if (reqResumeOptional.isEmpty()) {
            throw new ObjectNotExisted("Resume với id= " + id + " không tồn tại");
        }
        return ResponseEntity.ok().body(this.resumeService.getResume(reqResumeOptional.get()));
    }

    @GetMapping("/resumes")
    public ResponseEntity<ResultPaginationDTO> fetchAllResume(@Filter Specification<Resume> spec, Pageable pageable) {

        // Mỗi người dùng (HR) có một Company iD từ ID này ta có thể lấy ra tất cả các
        // Job thuộc công ty đó
        // Từ danh sách các Job thuộc công ty đó ta sẽ check Job id của resume
        // có trong danh sách ban đầu không -> nếu có thì tức là resume đó gửi vào công
        // ty ban đầu -> HR của công ty đó có thể xem
        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User currentUser = this.userService.handleGetUserByUserName(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    arrJobIds = companyJobs.stream().map(x -> x.getId()).collect(Collectors.toList());
                }
            }
        }

        Specification<Resume> jobInSpec = filterSpecificationConverter.convert(filterBuilder.field("job")
                .in(filterBuilder.input(arrJobIds)).get());

        Specification<Resume> finalSpecs = jobInSpec.and(spec);

        return ResponseEntity
                .ok(this.resumeService.handleFetchAllResumesWithSpecificationAndPagination(finalSpecs, pageable));
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a new Resume")
    public ResponseEntity<ResCreateResume> createResume(@Valid @RequestBody Resume resume) throws ObjectNotExisted {
        // Check validation
        if (!this.resumeService.checkResumeExistByUserAndJob(resume)) {
            throw new ObjectNotExisted("JobId or UserId Not Found");
        }
        Resume savedResume = this.resumeService.handleSaveResume(resume);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResCreateResume(savedResume.getId(), savedResume.getCreatedAt(), savedResume.getCreatedBy()));

    }

    @PutMapping("/resumes")
    @ApiMessage("update status of a resume")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody ReqUpdateResumeDTO reqUpdateResumeDTO)
            throws ObjectNotExisted {

        Optional<Resume> optionalResume = this.resumeService.handleFetchResumeById(reqUpdateResumeDTO.getId());
        if (optionalResume.isEmpty()) {
            throw new ObjectNotExisted("Resume với id= " + reqUpdateResumeDTO.getId() + " không tồn tại");
        }
        Resume realResume = optionalResume.get();
        realResume.setStatus(reqUpdateResumeDTO.getStatus());
        Resume updatedResume = this.resumeService.handleSaveResume(realResume);

        return ResponseEntity.ok(
                new ResUpdateResumeDTO(updatedResume.getUpdatedAt(), SecurityUtil.getCurrentUserLogin().orElse("?")));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("delete a resume by id")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws ObjectNotExisted {
        if (this.resumeService.handleFetchResumeById(id).isEmpty()) {
            throw new ObjectNotExisted("Resume với id= " + id + " không tồn tại");
        }
        this.resumeService.handleDeleteResumeById(id);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumesByUser(Pageable pageable) {

        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }

}
