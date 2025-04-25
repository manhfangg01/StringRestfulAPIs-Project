package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.service.CompanyService;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createACompany(@Valid @RequestBody Company company) {
        // Company newCompany = new Company();
        // newCompany.setName(company.getName());
        // newCompany.setAddress(company.getAddress());
        // newCompany.setDescription(company.getDescription());
        // newCompany.setLogo(company.getLogo());
        // newCompany.setCreatedAt(Instant.now());
        // newCompany.setUpdatedAt(null);
        // newCompany.setCreatedBy(null);
        // newCompany.setUpdatedBy(null);

        this.companyService.handleSaveCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(company);
        // ** Nhiệm vụ set các giá trị không có trong body JSON thường sẽ do các
        // annotation + callback function của hibernate đảm nhận (trừ trường id (auto))
        // -> xem ở module Company
    }

}
