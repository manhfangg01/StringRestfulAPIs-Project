package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.controller.ResumeController;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Optional<Role> handleFindRoleByName(String name) {
        return this.roleRepository.findByName(name);
    }

    public Role handleSaveRole(Role role) {
        return this.roleRepository.save(role);
    }

    public Role create(Role role) {
        if (role.getPermissions() != null) {
            List<Long> perId = role.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(perId);
            role.setPermissions(dbPermissions);
        }
        return this.roleRepository.save(role);
    }

    public Optional<Role> handleFindRoleById(long id) {
        return this.roleRepository.findById(id);
    }

    public Role update(Role role) {
        Role presentRole = this.roleRepository.findById(role.getId()).get();
        presentRole.setActive(role.isActive());
        presentRole.setName(role.getName());
        presentRole.setDescription(role.getDescription());
        if (role.getPermissions() != null) {
            List<Long> perId = role.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(perId);
            presentRole.setPermissions(dbPermissions);
        }

        return this.roleRepository.save(presentRole);
    }

    public ResultPaginationDTO handleFetchAllRolesWithSpecificationAndPagination(Specification<Role> specification,
            Pageable pageable) {
        ResultPaginationDTO paginationDTO = new ResultPaginationDTO();
        Page<Role> rolePage = this.roleRepository.findAll(specification, pageable);
        List<Role> roles = rolePage.getContent();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(rolePage.getNumber() + 1);
        meta.setPageSize(rolePage.getSize());
        meta.setPages(rolePage.getTotalPages());
        meta.setTotal(rolePage.getTotalElements());
        paginationDTO.setMeta(meta);
        paginationDTO.setResult(roles);
        return paginationDTO;
    }

    public void delete(long id) {
        this.roleRepository.deleteById(id);
    }

}
