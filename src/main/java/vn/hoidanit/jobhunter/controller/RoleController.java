package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.ObjectCollapsed;
import vn.hoidanit.jobhunter.util.error.ObjectNotExisted;

import java.util.List;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/roles")
    @ApiMessage("fetch all roles")
    public ResponseEntity<ResultPaginationDTO> getRoles(@Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.roleService.handleFetchAllRolesWithSpecificationAndPagination(spec, pageable));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch one roles with id")
    public ResponseEntity<Role> getRole(@PathVariable("id") long id) throws ObjectNotExisted {
        Optional<Role> role = roleService.handleFindRoleById(id);
        if (role.isEmpty()) {
            throw new ObjectNotExisted("Không tim thấy role với id: " + id);
        }

        return ResponseEntity.ok().body(role.get());
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> createRole(@RequestBody Role role) throws ObjectCollapsed {
        if (this.roleService.handleFindRoleByName(role.getName()).isPresent()) {
            throw new ObjectCollapsed("Role đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(role));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> updateRole(@RequestBody Role role) throws ObjectNotExisted {
        // check existed
        if (this.roleService.handleFindRoleById(role.getId()).isEmpty()) {
            throw new ObjectNotExisted("Role " + role.getId() + " không tồn tại");
        }

        // Không check name do có thể có case chỉ update Permission không update name

        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.update(role));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws ObjectNotExisted {
        if (this.roleService.handleFindRoleById(id).isEmpty()) {
            throw new ObjectNotExisted("role:  " + id + " không tồn tại");
        }
        this.roleService.delete(id);
        return ResponseEntity.ok(null);
        // Role là bên sở hữu nên một khi nó bị xóa thì permission cũng auto bị xóa
        // trong role_permission
    }

}
