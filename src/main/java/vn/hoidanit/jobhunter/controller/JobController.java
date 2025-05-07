package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.response.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.ObjectNotExisted;

import java.util.Optional;

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
public class JobController {
    private JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> getOneJob(@PathVariable("id") long id) throws ObjectNotExisted {
        Optional<Job> currentJob = this.jobService.handleFetchJobById(id);
        if (currentJob.isEmpty()) {
            throw new ObjectNotExisted("Không tìm thấy job id = " + id);
        }
        return ResponseEntity.ok().body(currentJob.get());

    }

    @GetMapping("/jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJob(@Filter Specification<Job> spec,
            Pageable pageable) {
        return ResponseEntity.ok()
                .body(this.jobService.handleFetchAllJobsWithSpecificationAndPagination(spec, pageable));
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a job")
    public ResponseEntity<vn.hoidanit.jobhunter.domain.response.ResCreateJobDTO> create(@Valid @RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.jobService.create(job));
    }

    @PutMapping("/jobs")
    @ApiMessage("Update a job")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@RequestBody Job job) throws ObjectNotExisted {
        Optional<Job> currentJob = this.jobService.handleFetchJobById(job.getId());
        if (currentJob.isEmpty()) {
            throw new ObjectNotExisted("Không tìm thấy job id = " + job.getId());
        }

        return ResponseEntity.ok().body(this.jobService.updateJob(job, currentJob.get()));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") long id) throws ObjectNotExisted {
        Optional<Job> currentJob = this.jobService.handleFetchJobById(id);
        if (currentJob.isEmpty()) {
            throw new ObjectNotExisted("Không tìm thấy job id = " + id);
        }
        return ResponseEntity.ok().body(null);
    }

}
