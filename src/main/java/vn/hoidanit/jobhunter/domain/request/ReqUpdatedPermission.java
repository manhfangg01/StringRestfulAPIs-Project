package vn.hoidanit.jobhunter.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdatedPermission {
    private long id;
    private String name;
    private String apiPath;
    private String method;
    private String module;
}
