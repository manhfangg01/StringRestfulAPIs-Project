package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.response.CompanyResponseDTO;
import vn.hoidanit.jobhunter.domain.dto.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.BusinessException;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;
import vn.hoidanit.jobhunter.util.error.ObjectNotExisted;

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
public class CompanyController {
    private final CompanyService companyService;
    private final UserService userService;

    public CompanyController(CompanyService companyService, UserService userService) {
        this.companyService = companyService;
        this.userService = userService;
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

    @GetMapping("/companies/{id}")
    public ResponseEntity<CompanyResponseDTO> getOneCompany(@PathVariable("id") long id) throws ObjectNotExisted {
        Optional<Company> optionalCompany = this.companyService.handleFetchCompanyById(id);
        CompanyResponseDTO companyResponse = new CompanyResponseDTO();

        if (optionalCompany.isEmpty()) {
            throw new ObjectNotExisted("Công ty không tồn tại");
        } else {
            Company realCompany = optionalCompany.get();
            companyResponse.setId(realCompany.getId());
            companyResponse.setName(realCompany.getName());
            companyResponse.setDescription(realCompany.getDescription());
            companyResponse.setAddress(realCompany.getAddress());
            companyResponse.setLogo(realCompany.getLogo());
            companyResponse.setCreatedAt(realCompany.getCreatedAt());
            companyResponse.setUpdatedAt(realCompany.getUpdatedAt());
        }
        return ResponseEntity.ok(companyResponse);
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
    public ResponseEntity<Void> deteleCompany(@PathVariable("id") long id) throws ObjectNotExisted, BusinessException {
        Optional<Company> optionalCompany = this.companyService.handleFetchCompanyById(id);
        if (optionalCompany.isEmpty()) {
            throw new ObjectNotExisted("Công ty không tồn tại");
        } else {

            long countEmployees = this.userService.handleCountUserInCompanyById(optionalCompany.get());
            if (countEmployees > 0) {
                throw new BusinessException("Không thể xóa bởi vì hiện tại vẫn đang có " + countEmployees
                        + " đang làm việc trong " + companyService.handleFetchCompanyById(id).get().getName());
            } else {
                this.companyService.handleDeleteCompany(id);
            }
        }
        return ResponseEntity.ok(null);
    }

}
