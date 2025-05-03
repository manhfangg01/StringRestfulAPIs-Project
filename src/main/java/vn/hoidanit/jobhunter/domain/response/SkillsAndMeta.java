package vn.hoidanit.jobhunter.domain.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.domain.Skill;

@Getter
@Setter
public class SkillsAndMeta {
    private List<Skill> skills;
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
