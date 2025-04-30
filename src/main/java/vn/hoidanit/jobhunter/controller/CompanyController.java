package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

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
    @ApiMessage("Fetch All Companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompaniesWithPaginationAndSpecification(
            @Filter Specification<Company> spec,
            Pageable pageable) {
        // Pageable tự handle page, size, sort, và filter, trong trường hợp không truyền
        // lên URL cững sẽ handle được
        return ResponseEntity.ok(this.companyService.fetchAllCompaniesWithPaginationAndSpecification(spec, pageable));
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
