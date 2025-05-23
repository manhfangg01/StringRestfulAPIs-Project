package vn.hoidanit.jobhunter.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.service.listener.AuditTrailListener;
import vn.hoidanit.jobhunter.util.SecurityUtil;

@Table(name = "companies")
@Getter
@Setter
@Entity
@EntityListeners(AuditTrailListener.class)
public class Company {

    // private final SecurityUtil securityUtil;

    // public Company(SecurityUtil securityUtil) {
    // this.securityUtil = securityUtil;
    // Không bao giờ được Inject 1 class vào trong 1 class có @Entity
    // Thay vào đó hãy dùng static
    // }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "Không được bỏ trống trường name")
    private String name;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private String address;
    private String logo;
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7") // Format
    // cho frontend thôi

    private Instant createdAt; // Khi deploy lên thì ở BACKEND múi giờ GMT sẽ được tự động chỉnh lại theo khu
                               // vực (Mặc
                               // định là 0)
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore // Nếu muốn response phớt lờ trường này, cách 2 là dùng DTO
    List<User> users;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Job> jobs;

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
