package vn.hoidanit.jobhunter.domain.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.domain.Job;

@Getter
@Setter
public class ResJobsAndMeta {
    private List<Job> jobs;
    private Meta meta;

    @Getter
    @Setter
    public static class Meta {
        private int page;
        private int pageSize;
        private int pages;
        private long total;

    }
}
