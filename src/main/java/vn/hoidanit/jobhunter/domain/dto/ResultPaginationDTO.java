package vn.hoidanit.jobhunter.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPaginationDTO {
    private Meta meta;
    private Object result;

    public ResultPaginationDTO(Meta meta, Object result) {
        this.meta = meta;
        this.result = result;
    }

    public ResultPaginationDTO() {
    }
}
