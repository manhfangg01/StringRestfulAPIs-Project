package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.request.ReqUpdatedPermission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.PermissionService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.ObjectCollapsed;
import vn.hoidanit.jobhunter.util.error.ObjectNotExisted;

import java.net.http.HttpResponse.ResponseInfo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/permissions")
    public ResponseEntity<ResultPaginationDTO> getMethodName(
            @Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity
                .ok(this.permissionService.handleFetchAllJobsWithSpecificationAndPagination(spec, pageable));
    }

    @PostMapping("/permissions")
    @ApiMessage("create a permission")
    public ResponseEntity<Permission> createPermission(@RequestBody Permission per) throws ObjectCollapsed {
        if (this.permissionService.isPermissionExist(per)) {
            throw new ObjectCollapsed("Permission đã tồn tại");
        }
        Permission savedPermission = this.permissionService.handleSavePermission(per);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPermission);
    }

    @PutMapping("/permissions")
    public ResponseEntity<Permission> updatePermission(@RequestBody Permission per)
            throws ObjectNotExisted, ObjectCollapsed {
        if (this.permissionService.handleFetchPermissionById(per.getId()).isEmpty()) {
            throw new ObjectNotExisted("Permission với id = " + per.getId() + " không tồn tại");
        }
        if (this.permissionService.isPermissionExist(per)) {
            throw new ObjectCollapsed("Permission đã tồn tại");
        }
        Permission updatedPermission = this.permissionService.handleSavePermission(per);
        return ResponseEntity.ok(updatedPermission);

    }

    // Permission không cần xóa vì

}
