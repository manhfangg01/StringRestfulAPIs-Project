package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission handleSavePermission(Permission resume) {
        return this.permissionRepository.save(resume);
    }

    public boolean isPermissionExist(Permission p) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(
                p.getModule(),
                p.getApiPath(),
                p.getMethod());
    }

    public Optional<Permission> handleFetchPermissionById(long id) {
        return this.permissionRepository.findById(id);
    }

    public ResultPaginationDTO handleFetchAllJobsWithSpecificationAndPagination(Specification<Permission> spec,
            Pageable pageable) {
        ResultPaginationDTO resJobsAndMeta = new ResultPaginationDTO();
        Page<Permission> permissionPage = this.permissionRepository.findAll(spec, pageable);
        List<Permission> permissions = permissionPage.getContent();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(permissionPage.getNumber() + 1); // PageNumber bắt đầu từ 0
        meta.setPageSize(permissionPage.getSize());
        meta.setPages(permissionPage.getTotalPages());
        meta.setTotal(permissionPage.getTotalElements()); // Không trừ 1
        resJobsAndMeta.setResult(permissions);
        resJobsAndMeta.setMeta(meta);
        return resJobsAndMeta;
    }
}
