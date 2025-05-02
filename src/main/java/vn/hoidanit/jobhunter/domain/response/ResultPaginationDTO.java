package vn.hoidanit.jobhunter.domain.response;

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

    @Getter
    @Setter
    public static class Meta {
        private int page;
        private int pageSize;
        private int pages;
        private long total;

    }

}
