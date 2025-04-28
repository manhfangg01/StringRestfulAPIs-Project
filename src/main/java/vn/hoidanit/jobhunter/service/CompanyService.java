package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
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

}
