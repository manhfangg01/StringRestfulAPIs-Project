package vn.hoidanit.jobhunter.domain.response;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.LevelEnum;

@Getter
@Setter
public class ResUpdateJobDTO {
    private long id;
    private String name;
    private String location;
    private int quantity;
    private double salary;
    private LevelEnum level;
    private Instant startDate;
    private Instant endDate;
    private boolean active;
    private Instant createdAt;
    private String createdBy;
    private List<String> skills;
    private Instant updatedBy;
    private Instant updatedAt;

}
