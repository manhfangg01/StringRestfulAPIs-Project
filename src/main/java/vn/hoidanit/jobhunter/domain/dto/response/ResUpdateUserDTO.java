package vn.hoidanit.jobhunter.domain.dto.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private GenderEnum gender;
    private int age;
    private String address;
    private Instant UpdatedAt;
    private CompanyDTO company;

    @Getter
    @Setter
    public static class CompanyDTO {
        private long id;
        private String name;
    }

}
