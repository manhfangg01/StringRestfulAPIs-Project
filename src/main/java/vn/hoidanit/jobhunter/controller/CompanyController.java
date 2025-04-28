package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createACompany(@Valid @RequestBody Company company) {
        this.companyService.handleSaveCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(company);
    }

    @GetMapping("/companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompany(
            @RequestParam("current") Optional<String> currentOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional) {
        String currentPage = currentOptional.isPresent() ? currentOptional.get() : "";
        String numberPage = pageSizeOptional.isPresent() ? pageSizeOptional.get() : "";
        ResultPaginationDTO result = new ResultPaginationDTO();
        System.out.println("---------------------------------- cr:" + currentPage + " num:" + numberPage);
        if (currentPage.equals("") || numberPage.equals("")) {
            result.setMeta(null);
            result.setResult(this.companyService.handleFetchAllCompanies());
            return ResponseEntity.ok(result);
        }
        int pageNumber = Integer.parseInt(currentPage);
        int pageSize = Integer.parseInt(numberPage);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Company> companyPaginating = this.companyService.handleFetchAllCompanyWithPagination(pageable);
        Meta mt = new Meta();

        mt.setPage(companyPaginating.getNumber());
        mt.setPageSize(companyPaginating.getSize());

        mt.setPages(companyPaginating.getTotalPages());
        mt.setTotal(companyPaginating.getTotalElements());
        result.setMeta(mt);
        result.setResult(companyPaginating.getContent());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@RequestBody Company checkCompany) throws IdInvalidException {
        Company updatedCompany = this.companyService.updateCompany(checkCompany);
        if (updatedCompany == null) {
            throw new IdInvalidException("Không tồn tại công ty phù hợp ứng với công ty muốn cập nhật");
        } else {
            return ResponseEntity.ok(updatedCompany);
        }
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deteleCompany(@PathVariable("id") long id) {
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.ok(null);
    }

}
