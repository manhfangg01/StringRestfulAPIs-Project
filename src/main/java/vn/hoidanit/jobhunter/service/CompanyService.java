package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public void handleSaveCompany(Company company) {
        this.companyRepository.save(company);
    }

    public List<Company> handleFetchAllCompanies() {
        return this.companyRepository.findAll();
    }

    public Optional<Company> handleFetchCompanyById(long id) {
        return this.companyRepository.findById(id);
    }

    public Company updateCompany(Company updateCompany) {
        Optional<Company> checkCompany = handleFetchCompanyById(updateCompany.getId());
        if (checkCompany.isPresent()) {
            Company realCompany = checkCompany.get();
            realCompany.setName(updateCompany.getName());
            realCompany.setAddress(updateCompany.getAddress());
            realCompany.setDescription(updateCompany.getDescription());
            realCompany.setLogo(updateCompany.getLogo());
            handleSaveCompany(realCompany);
            return realCompany;
        } else
            return null;

    }

    public void handleDeleteCompany(long id) {
        this.companyRepository.deleteById(id);
    }

    // Pagination
    public Page<Company> handleFetchAllCompanyWithPagination(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }

    public ResultPaginationDTO fetchAllCompaniesWithoutPagination() {
        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(null);
        result.setResult(this.handleFetchAllCompanies()); // phương thức cũ đã có
        return result;
    }

    public ResultPaginationDTO fetchAllCompaniesWithPagination(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Company> companyPage = this.handleFetchAllCompanyWithPagination(pageable);

        Meta meta = new Meta();
        meta.setPage(companyPage.getNumber() + 1); // +1 nếu muốn page client tính từ 1
        meta.setPageSize(companyPage.getSize());
        meta.setPages(companyPage.getTotalPages());
        meta.setTotal(companyPage.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(companyPage.getContent());
        return result;
    }

    public ResultPaginationDTO fetchAllCompaniesWithPaginationAndSpecification(Specification<Company> spec,
            Pageable pageable) {

        Page<Company> companyPage = this.companyRepository.findAll(spec, pageable);
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1); // +1 nếu muốn page client tính từ 1
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(companyPage.getTotalPages());
        meta.setTotal(companyPage.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(companyPage.getContent());
        return result;
    }

}
