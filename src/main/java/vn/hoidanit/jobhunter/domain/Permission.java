package vn.hoidanit.jobhunter.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.SecurityUtil;

@Entity
@Getter
@Setter
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "name không được để trống")
    private String name;
    @NotBlank(message = "apiPath không được để trống")
    private String apiPath;
    @NotBlank(message = "method không được để trống")
    private String method;
    @NotBlank(message = "module không được để trống")
    private String module;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions") // Nhiều Permissions thuộc nhiều Role chính là bên
                                                                  // được sở hữu

    // Permission do đang bị sở hữu nên nếu Permission bị xóa thì cũng chỉ mất nó
    // thôi không ảnh hưởng gì đến Role
    @JsonIgnore
    private List<Role> roles;

    @PrePersist
    public void handleBeforeCreationOfACompany() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "?";
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdateOfACompany() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "?";
        this.updatedAt = Instant.now();
    }

}
