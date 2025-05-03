package vn.hoidanit.jobhunter.domain.dto.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyResponseDTO {
    private long id;
    private String name;
    private String description;
    private String address;
    private String logo;
    private Instant createdAt;
    private Instant updatedAt;
}
